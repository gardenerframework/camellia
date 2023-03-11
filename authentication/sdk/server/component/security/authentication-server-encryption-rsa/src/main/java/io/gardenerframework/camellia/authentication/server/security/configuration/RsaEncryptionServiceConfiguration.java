package io.gardenerframework.camellia.authentication.server.security.configuration;

import io.gardenerframework.camellia.authentication.server.security.encryption.EncryptionService;
import io.gardenerframework.camellia.authentication.server.security.encryption.RsaEncryptionService;
import io.gardenerframework.fragrans.data.cache.client.CacheClient;
import io.gardenerframework.fragrans.data.cache.manager.BasicCacheManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnMissingBean(EncryptionService.class)
@ConditionalOnClass(BasicCacheManager.class)
@ConditionalOnBean(CacheClient.class)
@Import(RsaEncryptionService.class)
public class RsaEncryptionServiceConfiguration {
}
