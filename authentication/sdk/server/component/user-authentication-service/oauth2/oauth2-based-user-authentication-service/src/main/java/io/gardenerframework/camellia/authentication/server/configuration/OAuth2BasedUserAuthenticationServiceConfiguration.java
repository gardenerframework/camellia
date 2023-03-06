package io.gardenerframework.camellia.authentication.server.configuration;

import io.gardenerframework.camellia.authentication.server.main.CachedOAuth2StateStore;
import io.gardenerframework.camellia.authentication.server.main.OAuth2StateStore;
import io.gardenerframework.camellia.authentication.server.main.endpoint.OAuth2StateEndpoint;
import io.gardenerframework.fragrans.data.cache.client.CacheClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author zhanghan30
 * @date 2023/3/6 18:33
 */
@Configuration
@Import({
        OAuth2BasedUserAuthenticationServiceConfiguration.OAuth2StateStoreAutoConfiguration.class,
        OAuth2StateEndpoint.class
})
public class OAuth2BasedUserAuthenticationServiceConfiguration {
    @Configuration
    @ConditionalOnMissingBean(OAuth2StateStore.class)
    @ConditionalOnClass(CacheManager.class)
    @ConditionalOnBean(CacheClient.class)
    public static class OAuth2StateStoreAutoConfiguration {
        @Bean
        public OAuth2StateStore defaultOAuth2StateStore(CacheClient client) {
            return new CachedOAuth2StateStore(client);
        }
    }
}
