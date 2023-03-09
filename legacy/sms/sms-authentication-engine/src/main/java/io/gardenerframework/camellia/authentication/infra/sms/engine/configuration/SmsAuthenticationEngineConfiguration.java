package io.gardenerframework.camellia.authentication.infra.sms.engine.configuration;

import io.gardenerframework.camellia.authentication.infra.sms.core.SmsAuthenticationCodeStore;
import io.gardenerframework.camellia.authentication.infra.sms.engine.service.SmsAuthenticationService;
import io.gardenerframework.camellia.authentication.infra.sms.engine.support.CachedSmsAuthenticationCodeStore;
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
 * @date 2023/2/14 19:18
 */
@Configuration
@Import(SmsAuthenticationEngineConfiguration.CachedSmsAuthenticationCodeStoreAutoConfiguration.class)
public class SmsAuthenticationEngineConfiguration {
    @Bean
    @ConditionalOnMissingBean(SmsAuthenticationService.class)
    public SmsAuthenticationService smsAuthenticationService() {
        return new SmsAuthenticationService();
    }

    @Configuration
    @ConditionalOnClass(BasicCacheManager.class)
    @ConditionalOnMissingBean(value = {SmsAuthenticationCodeStore.class})
    @ConditionalOnBean(CacheClient.class)
    public static class CachedSmsAuthenticationCodeStoreAutoConfiguration {
        @Bean
        public SmsAuthenticationCodeStore smsAuthenticationCodeStore(CacheClient cacheClient) {
            return new CachedSmsAuthenticationCodeStore(cacheClient);
        }
    }
}
