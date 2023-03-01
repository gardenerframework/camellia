package com.jdcloud.gardener.camellia.authorization.demo.sms.configuration;

import com.jdcloud.gardener.camellia.sms.authentication.SmsAuthenticationCodeClient;
import com.jdcloud.gardener.camellia.sms.authentication.configuration.JdCloudSmsAuthenticationCodeClientOption;
import com.jdcloud.gardener.camellia.sms.authentication.configuration.JdCloudSmsClientAccessKeyOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

/**
 * @author ZhangHan
 * @date 2022/5/16 9:18
 */
@Configuration
@ConditionalOnClass(SmsAuthenticationCodeClient.class)
public class DemoSmsAuthenticationConfiguration {
    public DemoSmsAuthenticationConfiguration(
            JdCloudSmsAuthenticationCodeClientOption jdCloudSmsAuthenticationCodeClientOption,
            JdCloudSmsClientAccessKeyOptions jdCloudSmsClientAccessKeyOptions
    ) {
        jdCloudSmsClientAccessKeyOptions.setAccessKey("14574E9048ED06FAEDA10E7C873F8529");
        jdCloudSmsClientAccessKeyOptions.setAccessKeyId("7BC27F6DF3A3DE64E94199BDF3FCBAB1");
        jdCloudSmsAuthenticationCodeClientOption.setAuthenticationCodeMessageSignId("qm_f58d5574f1fb43a0b04333b61bb74c14");
        jdCloudSmsAuthenticationCodeClientOption.setAuthenticationCodeMessageTemplateId("mb_47175bac722446ba8eb81819bc3a49a1");
    }
}
