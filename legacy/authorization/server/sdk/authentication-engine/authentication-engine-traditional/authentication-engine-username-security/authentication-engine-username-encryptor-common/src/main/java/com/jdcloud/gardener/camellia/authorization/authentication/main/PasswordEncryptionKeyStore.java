package com.jdcloud.gardener.camellia.authorization.authentication.main;

import org.springframework.lang.Nullable;

import java.time.Duration;

public interface PasswordEncryptionKeyStore {
    void save(String id, String key, Duration ttl);

    @Nullable
    String load(String id);

    /**
     * 获取还剩下的时间
     *
     * @param id key id
     * @return 剩余时间(以秒为单位)，为空就是已经过期了
     */
    @Nullable
    Long getTtl(String id);
}
