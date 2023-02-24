package io.gardenerframework.camellia.authentication.server.configuration;

import io.gardenerframework.camellia.authentication.server.common.api.group.AuthorizationServerRestControllerGroupConfigurer;
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
        //配置累
        AuthenticationServerEngineSecurityConfiguration.class,
        AuthenticationServerEngineOAuth2Configuration.class,
        AuthenticationServerEngineManagementConfiguration.class,
        //api分组配置
        AuthorizationServerRestControllerGroupConfigurer.class
})
public class AuthenticationServerEngineConfiguration {
}
