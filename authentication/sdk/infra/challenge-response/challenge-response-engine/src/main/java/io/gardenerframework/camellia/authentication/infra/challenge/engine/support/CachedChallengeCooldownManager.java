package io.gardenerframework.camellia.authentication.infra.challenge.engine.support;

import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeCooldownManager;
import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.fragrans.data.cache.manager.BasicCacheManager;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.time.Duration;

/**
 * @author zhanghan30
 * @date 2023/2/21 17:59
 */
@AllArgsConstructor
public class CachedChallengeCooldownManager implements ChallengeCooldownManager, NullRequestingClientIdProvider {
    private static final String CHALLENGE_COOLDOWN_SUFFIX = "cooldown";
    @NonNull
    private final BasicCacheManager<String> cacheManager;

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
                "cooldown",
                "challenge",
                getClientId(client),
                scenario.getCanonicalName()
        };
    }

    @Nullable
    @Override
    public Duration getTimeRemaining(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String timerId
    ) throws Exception {
        return cacheManager.ttl(
                buildNamespace(client, scenario),
                timerId,
                CHALLENGE_COOLDOWN_SUFFIX
        );
    }

    @Override
    public boolean startCooldown(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String timerId,
            @NonNull Duration ttl
    ) throws Exception {
        return cacheManager.setIfNotPresents(
                buildNamespace(client, scenario),
                timerId,
                CHALLENGE_COOLDOWN_SUFFIX,
                timerId,
                ttl
        );
    }
}
