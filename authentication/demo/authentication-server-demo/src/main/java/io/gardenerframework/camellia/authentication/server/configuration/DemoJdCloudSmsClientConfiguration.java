package io.gardenerframework.camellia.authentication.server.configuration;

import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.client.JdCloudSmsClient;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.configuration.JdCloudSmsClientSecurityOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

/**
 * @author ZhangHan
 * @date 2022/5/16 9:18
 */
@Configuration
public class DemoJdCloudSmsClientConfiguration {
    public DemoJdCloudSmsClientConfiguration(
            JdCloudSmsClientSecurityOption jdCloudSmsClientSecurityOption
    ) {
        jdCloudSmsClientSecurityOption.setAccessKey("14574E9048ED06FAEDA10E7C873F8529");
        jdCloudSmsClientSecurityOption.setAccessKeyId("7BC27F6DF3A3DE64E94199BDF3FCBAB1");
    }

    @Bean
    public JdCloudSmsClient.SmsVerificationCodeTemplateProvider smsVerificationCodeTemplateProvider() {
        return new JdCloudSmsClient.SmsVerificationCodeTemplateProvider() {

            @Override
            public String getSignId(@Nullable RequestingClient client, Class<? extends Scenario> scenario) {
                return "qm_f58d5574f1fb43a0b04333b61bb74c14";
            }

            @Override
            public String getTemplateId(@Nullable RequestingClient client, Class<? extends Scenario> scenario) {
                return "mb_47175bac722446ba8eb81819bc3a49a1";
            }
        };
    }
}
