package com.jdcloud.gardener.camellia.authorization.authentication.main;

import com.jdcloud.gardener.fragrans.data.cache.client.CacheClient;
import com.jdcloud.gardener.fragrans.data.cache.manager.BasicCacheManager;
import org.springframework.lang.Nullable;

import java.time.Duration;

public class RedisPasswordEncryptionKeyStore implements PasswordEncryptionKeyStore {
    private final String[] namespace = new String[]{"camellia", "authorization", "authentication", "engine", "username", "security", "key"};
    private final BasicCacheManager<String> cacheManager;

    public RedisPasswordEncryptionKeyStore(CacheClient client) {
        this.cacheManager = new BasicCacheManager<String>(client) {
        };
    }

    @Override
    public void save(String id, String key, Duration ttl) {
        this.cacheManager.set(namespace, id, null, key, ttl);
    }

    @Nullable
    @Override
    public String load(String id) {
        return this.cacheManager.get(namespace, id, null);
    }

    @Nullable
    @Override
    public Long getTtl(String id) {
        Duration ttl = this.cacheManager.ttl(namespace, id, null);
        return ttl == null ? null : ttl.getSeconds();
    }
}
