package io.gardenerframework.camellia.authentication.infra.challenge.engine.support;

import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.fragrans.data.cache.manager.BasicCacheManager;
import lombok.NonNull;

/**
 * @author zhanghan30
 * @date 2023/2/22 17:27
 */
public class GenericCachedChallengeStore extends CachedChallengeStoreTemplate<Challenge> {
    public GenericCachedChallengeStore(@NonNull BasicCacheManager<Challenge> challengeCacheManager, @NonNull BasicCacheManager<String> challengeIdCacheManager, @NonNull BasicCacheManager<Boolean> challengeVerifiedFlagCacheManager) {
        super(challengeCacheManager, challengeIdCacheManager, challengeVerifiedFlagCacheManager);
    }
}
