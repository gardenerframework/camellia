package io.gardenerframework.camellia.authentication.infra.challenge.engine.support;

import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeContextStore;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeContext;
import io.gardenerframework.fragrans.data.cache.manager.BasicCacheManager;
import lombok.NonNull;

/**
 * @author zhanghan30
 * @date 2023/2/22 17:33
 */
public class GenericCachedChallengeContextStore extends CachedChallengeContextStoreTemplate<ChallengeContext> {
    public GenericCachedChallengeContextStore(@NonNull BasicCacheManager<ChallengeContext> challengeContextCacheManager) {
        super(challengeContextCacheManager);
    }

    @SuppressWarnings("unchecked")
    public <X extends ChallengeContext> ChallengeContextStore<X> migrateType() {
        return (ChallengeContextStore<X>) this;
    }
}
