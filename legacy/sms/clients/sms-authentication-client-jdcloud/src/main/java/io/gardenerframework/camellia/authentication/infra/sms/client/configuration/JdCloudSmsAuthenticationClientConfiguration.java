package io.gardenerframework.camellia.authentication.infra.sms.client.configuration;

import io.gardenerframework.camellia.authentication.infra.sms.client.jdcloud.JdCloudSmsAuthenticationClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author zhanghan30
 * @date 2023/2/16 11:53
 */
@Configuration
@Import({JdCloudSmsAuthenticationClientSecurityOption.class, JdCloudSmsAuthenticationClient.class})
public class JdCloudSmsAuthenticationClientConfiguration {
}
