package io.gardenerframework.camellia.authentication.infra.sms.client.test.utils;

import io.gardenerframework.camellia.authentication.infra.sms.client.configuration.JdCloudSmsAuthenticationClientSecurityOption;
import org.springframework.stereotype.Component;

@Component
public class JdCloudSmsAuthenticationClientSecurityOptionsInitializer {
    public JdCloudSmsAuthenticationClientSecurityOptionsInitializer(JdCloudSmsAuthenticationClientSecurityOption option) {
        option.setAccessKeyId("7BC27F6DF3A3DE64E94199BDF3FCBAB1");
        option.setAccessKey("14574E9048ED06FAEDA10E7C873F8529");
    }
}
