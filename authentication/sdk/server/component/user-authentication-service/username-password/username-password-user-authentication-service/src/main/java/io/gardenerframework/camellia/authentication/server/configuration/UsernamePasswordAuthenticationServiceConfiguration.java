package io.gardenerframework.camellia.authentication.server.configuration;

import io.gardenerframework.camellia.authentication.server.main.UsernamePasswordAuthenticationService;
import io.gardenerframework.camellia.authentication.server.main.UsernameResolver;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.UsernamePrincipal;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(UsernamePasswordAuthenticationServiceConfiguration.UsernamePasswordAuthenticationServiceAutoConfiguration.class)
public class UsernamePasswordAuthenticationServiceConfiguration {
    @Bean
    @ConditionalOnMissingBean(UsernameResolver.class)
    public UsernameResolver defaultUsernameResolver() {
        return (username, principalType) -> UsernamePrincipal.builder().name(username).build();
    }

    @Configuration
    @Import(UsernamePasswordAuthenticationService.class)
    @ConditionalOnMissingBean(UsernamePasswordAuthenticationService.class)
    public static class UsernamePasswordAuthenticationServiceAutoConfiguration {

    }
}
