package io.gardenerframework.camellia.authentication.infra.challenge.engine.configuration;

import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeStore;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.support.CachedChallengeStore;
import io.gardenerframework.fragrans.data.cache.client.CacheClient;
import io.gardenerframework.fragrans.data.cache.manager.BasicCacheManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhanghan30
 * @date 2023/2/21 17:17
 */
@Configuration
public class ChallengeResponseEngineConfiguration {
    @Configuration
    @ConditionalOnClass(BasicCacheManager.class)
    @ConditionalOnBean(CacheClient.class)
    public static class ChallengeResponseStoreAutoConfiguration {
        @Bean
        public ChallengeStore challengeStore(CacheClient cacheClient) {
            return new CachedChallengeStore(
                    new BasicCacheManager<Challenge>(cacheClient) {
                    },
                    new BasicCacheManager<String>(cacheClient) {
                    },
                    new BasicCacheManager<Boolean>(cacheClient) {
                    }
            );
        }
    }
}
