package io.gardenerframework.camellia.authentication.infra.sms.client.jdcloud;

import com.jdcloud.sdk.auth.ICredentials;
import com.jdcloud.sdk.http.HttpRequestConfig;
import com.jdcloud.sdk.http.Protocol;
import com.jdcloud.sdk.model.ServiceError;
import com.jdcloud.sdk.service.sms.client.SmsClient;
import com.jdcloud.sdk.service.sms.model.BatchSendRequest;
import com.jdcloud.sdk.service.sms.model.BatchSendResponse;
import com.jdcloud.sdk.service.sms.model.BatchSendResult;
import io.gardenerframework.camellia.authentication.infra.common.Scenario;
import io.gardenerframework.camellia.authentication.infra.sms.client.configuration.JdCloudSmsAuthenticationClientSecurityOption;
import io.gardenerframework.camellia.authentication.infra.sms.core.SmsAuthenticationClient;
import io.gardenerframework.fragrans.log.GenericOperationLogger;
import io.gardenerframework.fragrans.log.common.schema.state.Failed;
import io.gardenerframework.fragrans.log.common.schema.verb.Send;
import io.gardenerframework.fragrans.log.schema.content.GenericOperationLogContent;
import io.gardenerframework.fragrans.log.schema.details.Detail;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

/**
 * @author zhanghan30
 * @date 2023/2/16 11:18
 */
@Slf4j
public class JdCloudSmsAuthenticationClient implements SmsAuthenticationClient, InitializingBean {
    /**
     * 京东云的上线region
     */
    private static final String REGION = "cn-north-1";
    /**
     * sms客户端
     */
    private SmsClient smsClient;
    /**
     * ak/sk选项
     */
    @Setter(value = AccessLevel.PRIVATE, onMethod = @__({@Autowired}))
    private JdCloudSmsAuthenticationClientSecurityOption securityOption;
    /**
     * 基于应用、场景选择模板
     */
    @Setter(value = AccessLevel.PRIVATE, onMethod = @__({@Autowired}))
    private JdCloudSmsTemplateProvider templateProvider;
    /**
     * 日志
     */
    @Setter(value = AccessLevel.PRIVATE, onMethod = @__({@Autowired}))
    private GenericOperationLogger operationLogger;

    @Override
    public void sendCode(String applicationId, String mobilePhoneNumber, Class<? extends Scenario> scenario, String code) throws Exception {
        BatchSendRequest sendSmsRequest = new BatchSendRequest();
        sendSmsRequest.setPhoneList(Collections.singletonList(mobilePhoneNumber));
        sendSmsRequest.setRegionId(REGION);
        sendSmsRequest.setSignId(templateProvider.getSignId(applicationId, scenario));
        sendSmsRequest.setTemplateId(templateProvider.getTemplateId(applicationId, scenario));
        sendSmsRequest.setParams(Collections.singletonList(code));
        BatchSendResponse batchSendResponse = smsClient.batchSend(sendSmsRequest);
        ServiceError error = batchSendResponse.getError();
        if (error != null) {
            operationLogger.error(
                    log,
                    GenericOperationLogContent.builder()
                            .what(JdCloudSmsAuthenticationClient.class)
                            .operation(new Send())
                            .state(new Failed())
                            .detail(new Detail() {
                                private final int code = error.getCode();
                                private final String status = error.getStatus();
                                private final String message = error.getMessage();
                                private final String requestId = batchSendResponse.getRequestId();
                            }).build(),
                    null
            );
            throw new RuntimeException("send sms failed: " + error.getMessage());
        } else {
            BatchSendResult result = batchSendResponse.getResult();
            if (!Boolean.TRUE.equals(result.getStatus())) {
                operationLogger.error(
                        log,
                        GenericOperationLogContent.builder()
                                .what(JdCloudSmsAuthenticationClient.class)
                                .operation(new Send())
                                .state(new Failed())
                                .detail(new Detail() {
                                    private final String code = result.getCode();
                                    private final String message = result.getMessage();
                                }).build(),
                        null
                );
                throw new RuntimeException("send sms failed: " + result.getMessage());
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.smsClient = SmsClient.builder()
                .credentialsProvider(() -> new ICredentials() {
                    @Override
                    public String accessKeyId() {
                        return securityOption.getAccessKeyId();
                    }

                    @Override
                    public String secretAccessKey() {
                        return securityOption.getAccessKey();
                    }
                })
                .httpRequestConfig(new HttpRequestConfig.Builder().protocol(Protocol.HTTP).build())
                .build();
    }
}
