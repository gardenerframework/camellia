package io.gardenerframework.camellia.authentication.infra.sms.challenge.client;

import com.jdcloud.sdk.auth.ICredentials;
import com.jdcloud.sdk.http.HttpRequestConfig;
import com.jdcloud.sdk.http.Protocol;
import com.jdcloud.sdk.model.ServiceError;
import com.jdcloud.sdk.service.sms.client.SmsClient;
import com.jdcloud.sdk.service.sms.model.BatchSendRequest;
import com.jdcloud.sdk.service.sms.model.BatchSendResponse;
import com.jdcloud.sdk.service.sms.model.BatchSendResult;
import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.configuration.JdCloudSmsClientSecurityOption;
import io.gardenerframework.fragrans.log.GenericOperationLogger;
import io.gardenerframework.fragrans.log.common.schema.state.Failed;
import io.gardenerframework.fragrans.log.common.schema.verb.Send;
import io.gardenerframework.fragrans.log.schema.content.GenericOperationLogContent;
import io.gardenerframework.fragrans.log.schema.details.Detail;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;

import java.util.Collections;

@RequiredArgsConstructor
@Slf4j
public class JdCloudSmsClient implements SmsVerificationCodeClient, InitializingBean {
    /**
     * 京东云的上线region
     */
    private static final String REGION = "cn-north-1";
    /**
     * sms客户端
     */
    private SmsClient smsClient;
    @NonNull
    private final JdCloudSmsClientSecurityOption securityOption;
    @NonNull
    private final SmsVerificationCodeTemplateProvider smsVerificationCodeTemplateProvider;
    @NonNull
    private final GenericOperationLogger operationLogger;

    @Override
    public void sendVerificationCode(@Nullable RequestingClient client, @NonNull String mobilePhoneNumber, @NonNull Class<? extends Scenario> scenario, @NonNull String code) throws Exception {
        BatchSendRequest sendSmsRequest = new BatchSendRequest();
        sendSmsRequest.setPhoneList(Collections.singletonList(mobilePhoneNumber));
        sendSmsRequest.setRegionId(REGION);
        sendSmsRequest.setSignId(smsVerificationCodeTemplateProvider.getSignId(client, scenario));
        sendSmsRequest.setTemplateId(smsVerificationCodeTemplateProvider.getTemplateId(client, scenario));
        sendSmsRequest.setParams(Collections.singletonList(code));
        BatchSendResponse batchSendResponse = smsClient.batchSend(sendSmsRequest);
        ServiceError error = batchSendResponse.getError();
        if (error != null) {
            operationLogger.error(
                    log,
                    GenericOperationLogContent.builder()
                            .what(JdCloudSmsClient.class)
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
                                .what(JdCloudSmsClient.class)
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


    public interface SmsVerificationCodeTemplateProvider {
        /**
         * 返回签名id
         *
         * @param client   应用id
         * @param scenario 场景
         * @return 签名id
         */
        String getSignId(@Nullable RequestingClient client, Class<? extends Scenario> scenario);

        /**
         * 返回模板id
         *
         * @param client   应用id
         * @param scenario 场景
         * @return 模板id
         */
        String getTemplateId(@Nullable RequestingClient client, Class<? extends Scenario> scenario);
    }
}