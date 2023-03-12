package io.gardenerframework.camellia.authentication.server.security.configuration;

import io.gardenerframework.camellia.authentication.server.security.encryption.RsaEncryptionService;
import io.gardenerframework.fragrans.data.cache.client.CacheClient;
import io.gardenerframework.fragrans.data.cache.manager.BasicCacheManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ConditionalOnClass(BasicCacheManager.class)
@ConditionalOnBean(CacheClient.class)
//这个生成的bean的优先级较高，有助于解决onConditional没有的问题
@ComponentScan(
        basePackageClasses = RsaEncryptionService.class,
        includeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = RsaEncryptionService.class
                )
        }
)
public class RsaEncryptionServiceConfiguration {
}
