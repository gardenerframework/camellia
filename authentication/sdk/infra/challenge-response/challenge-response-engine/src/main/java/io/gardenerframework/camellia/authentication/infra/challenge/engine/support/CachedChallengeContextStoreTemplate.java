package io.gardenerframework.camellia.authentication.infra.challenge.engine.support;

import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeContextStore;
import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeContext;
import io.gardenerframework.fragrans.data.cache.manager.BasicCacheManager;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.time.Duration;

/**
 * @author zhanghan30
 * @date 2023/2/21 17:51
 */
@AllArgsConstructor
public class CachedChallengeContextStoreTemplate<X extends ChallengeContext> implements ChallengeContextStore<X> {
    private static final String CHALLENGE_CONTEXT_SUFFIX = "context";
    @NonNull
    private final BasicCacheManager<X> challengeContextCacheManager;

    protected String[] buildNamespace(
            @NonNull String applicationId,
            @NonNull Class<? extends Scenario> scenario
    ) {
        return new String[]{
                "camellia",
                "authentication",
                "infra",
                "challenge-response",
                "core",
                "store",
                "challenge-context",
                applicationId,
                scenario.getCanonicalName()
        };
    }

    @Override
    public void saveContext(
            @NonNull String applicationId,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId,
            @NonNull X context,
            @NonNull Duration ttl
    ) throws Exception {
        challengeContextCacheManager.set(
                buildNamespace(
                        applicationId,
                        scenario
                ),
                challengeId,
                CHALLENGE_CONTEXT_SUFFIX,
                context,
                ttl
        );
    }

    @Nullable
    @Override
    public X loadContext(@NonNull String applicationId, @NonNull Class<? extends Scenario> scenario, @NonNull String challengeId) throws Exception {
        return challengeContextCacheManager.get(
                buildNamespace(
                        applicationId,
                        scenario
                ),
                challengeId,
                CHALLENGE_CONTEXT_SUFFIX
        );
    }

    @Override
    public void removeContext(@NonNull String applicationId, @NonNull Class<? extends Scenario> scenario, @NonNull String challengeId) throws Exception {
        challengeContextCacheManager.delete(
                buildNamespace(
                        applicationId,
                        scenario
                ),
                challengeId,
                CHALLENGE_CONTEXT_SUFFIX
        );
    }
}
