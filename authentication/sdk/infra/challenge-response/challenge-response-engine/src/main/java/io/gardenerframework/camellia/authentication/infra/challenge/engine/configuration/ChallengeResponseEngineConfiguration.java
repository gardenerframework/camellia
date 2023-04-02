package io.gardenerframework.camellia.authentication.infra.challenge.engine.configuration;

import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeContextStore;
import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeCooldownManager;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeContext;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.support.CachedChallengeCooldownManager;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.support.ChallengeAuthenticatorNameInjector;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.support.GenericCachedChallengeContextStore;
import io.gardenerframework.fragrans.data.cache.client.CacheClient;
import io.gardenerframework.fragrans.data.cache.manager.BasicCacheManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author zhanghan30
 * @date 2023/2/21 17:17
 */
@Configuration
@Import({
        ChallengeAuthenticatorNameInjector.class
})
public class ChallengeResponseEngineConfiguration {
    @Configuration
    @ConditionalOnClass(BasicCacheManager.class)
    @ConditionalOnBean(CacheClient.class)
    public static class ChallengeResponseStoreAutoConfiguration {

        @Bean
        @ConditionalOnMissingBean(ChallengeContextStore.class)
        public GenericCachedChallengeContextStore challengeContextStore(CacheClient cacheClient) {
            return new GenericCachedChallengeContextStore(
                    new BasicCacheManager<ChallengeContext>(cacheClient) {
                    }
            );
        }

        @Bean
        @ConditionalOnMissingBean(ChallengeCooldownManager.class)
        public ChallengeCooldownManager challengeCooldownManager(CacheClient cacheClient) {
            return new CachedChallengeCooldownManager(new BasicCacheManager<String>(cacheClient) {
            });
        }
    }
}
