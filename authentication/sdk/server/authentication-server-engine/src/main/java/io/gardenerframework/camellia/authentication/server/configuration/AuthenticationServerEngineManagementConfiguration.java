package io.gardenerframework.camellia.authentication.server.configuration;

import io.gardenerframework.camellia.authentication.server.management.endpoint.UserAuthenticationServiceManagementEndpoint;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author zhanghan30
 * @date 2023/2/24 16:12
 */
@Configuration
@Import({
        UserAuthenticationServiceManagementEndpoint.class
})
public class AuthenticationServerEngineManagementConfiguration {
}
