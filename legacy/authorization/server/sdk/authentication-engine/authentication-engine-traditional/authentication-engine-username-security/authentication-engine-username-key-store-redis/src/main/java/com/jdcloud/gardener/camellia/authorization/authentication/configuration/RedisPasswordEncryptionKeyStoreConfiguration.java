package com.jdcloud.gardener.camellia.authorization.authentication.configuration;

import com.jdcloud.gardener.camellia.authorization.authentication.main.RedisPasswordEncryptionKeyStore;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(RedisPasswordEncryptionKeyStore.class)
public class RedisPasswordEncryptionKeyStoreConfiguration {
}
