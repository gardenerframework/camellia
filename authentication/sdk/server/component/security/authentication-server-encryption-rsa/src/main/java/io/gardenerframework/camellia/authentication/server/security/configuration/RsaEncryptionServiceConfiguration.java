package io.gardenerframework.camellia.authentication.server.security.configuration;

import io.gardenerframework.camellia.authentication.server.security.encryption.EncryptionService;
import io.gardenerframework.camellia.authentication.server.security.encryption.RsaEncryptionService;
import io.gardenerframework.fragrans.data.cache.client.CacheClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnMissingBean(EncryptionService.class)
public class RsaEncryptionServiceConfiguration {
    @Bean
    public EncryptionService rsaEncryptionService(CacheClient client) {
        return new RsaEncryptionService(client);
    }
}
