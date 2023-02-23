package io.gardenerframework.camellia.authentication.infra.challenge.engine.support;

import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeContextStore;
import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeContext;
import io.gardenerframework.camellia.authentication.infra.client.schema.RequestingClient;
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
public class CachedChallengeContextStoreTemplate<X extends ChallengeContext> implements ChallengeContextStore<X>, NullRequestingClientIdProvider {
    private static final String CHALLENGE_CONTEXT_SUFFIX = "context";
    @NonNull
    private final BasicCacheManager<X> challengeContextCacheManager;

    protected String[] buildNamespace(
            @Nullable RequestingClient client,
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
                getClientId(client),
                scenario.getCanonicalName()
        };
    }

    @Override
    public void saveContext(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId,
            @NonNull X context,
            @NonNull Duration ttl
    ) throws Exception {
        challengeContextCacheManager.set(
                buildNamespace(
                        client,
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
    public X loadContext(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId
    ) throws Exception {
        return challengeContextCacheManager.get(
                buildNamespace(
                        client,
                        scenario
                ),
                challengeId,
                CHALLENGE_CONTEXT_SUFFIX
        );
    }

    @Override
    public void removeContext(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId
    ) throws Exception {
        challengeContextCacheManager.delete(
                buildNamespace(
                        client,
                        scenario
                ),
                challengeId,
                CHALLENGE_CONTEXT_SUFFIX
        );
    }
}
