package io.gardenerframework.camellia.authentication.server.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 主配置类
 *
 * @author zhanghan30
 * @date 2023/2/23 14:50
 */
@Configuration
@Import({
        AuthenticationServerEngineSecurityConfiguration.class,
        AuthenticationServerEngineOAuth2Configuration.class
})
public class AuthenticationServerEngineConfiguration {
}
