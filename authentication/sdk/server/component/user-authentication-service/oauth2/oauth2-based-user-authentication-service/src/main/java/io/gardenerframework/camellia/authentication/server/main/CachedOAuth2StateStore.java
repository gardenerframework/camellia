package io.gardenerframework.camellia.authentication.server.main;

import io.gardenerframework.fragrans.data.cache.client.CacheClient;
import io.gardenerframework.fragrans.data.cache.manager.BasicCacheManager;
import lombok.NonNull;
import org.apache.commons.lang3.ArrayUtils;

import java.time.Duration;
import java.util.Objects;

/**
 * @author zhanghan30
 * @date 2023/3/6 18:14
 */
public class CachedOAuth2StateStore implements OAuth2StateStore {
    private static final String SUFFIX = "state";

    private static final String[] NAMESPACE = new String[]{
            "camellia",
            "authentication",
            "server",
            "component",
            "user-authentication-service",
            "oauth2"
    };
    @NonNull
    private final BasicCacheManager<String> stateCacheManager;

    public CachedOAuth2StateStore(@NonNull CacheClient client) {
        stateCacheManager = new BasicCacheManager<String>(client) {
        };
    }

    @Override
    public void save(@NonNull Class<? extends OAuth2BaseUserAuthenticationService> service, @NonNull String state, Duration ttl) throws Exception {
        stateCacheManager.set(getNamespace(service), state, SUFFIX, state, ttl);
    }

    @Override
    public boolean verify(@NonNull Class<? extends OAuth2BaseUserAuthenticationService> service, @NonNull String state) throws Exception {
        String saved = stateCacheManager.get(getNamespace(service), state, SUFFIX);
        if (!Objects.equals(saved, state)) {
            return false;
        } else {
            stateCacheManager.delete(getNamespace(service), state, SUFFIX);
            return true;
        }
    }

    protected String[] getNamespace(@NonNull Class<? extends OAuth2BaseUserAuthenticationService> service) {
        return ArrayUtils.add(NAMESPACE, service.getCanonicalName());
    }
}
