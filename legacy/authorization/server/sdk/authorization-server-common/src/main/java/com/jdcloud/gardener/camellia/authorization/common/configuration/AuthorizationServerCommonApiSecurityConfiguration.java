package com.jdcloud.gardener.camellia.authorization.common.configuration;

import com.jdcloud.gardener.camellia.authorization.common.api.security.schema.AccessTokenDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author ZhangHan
 * @date 2022/5/14 2:58
 */
@Configuration
public class AuthorizationServerCommonApiSecurityConfiguration {
    @Bean
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public AccessTokenDetails accessTokenDetails() {
        return new AccessTokenDetails();
    }
}
