package io.gardenerframework.camellia.authentication.infra.challenge.engine.support;

import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeStore;
import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.client.schema.RequestingClient;
import io.gardenerframework.fragrans.data.cache.manager.BasicCacheManager;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.time.Duration;

/**
 * @author zhanghan30
 * @date 2023/2/21 16:25
 */
@AllArgsConstructor
public class CachedChallengeStoreTemplate<C extends Challenge> implements ChallengeStore<C>, NullRequestingClientIdProvider {
    private static final String REQUEST_SIGNATURE_SUFFIX = "requestSignature";
    private static final String CHALLENGE_SUFFIX = "challenge";
    @NonNull
    private final BasicCacheManager<C> challengeCacheManager;
    @NonNull
    private final BasicCacheManager<String> challengeIdCacheManager;

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
                "challenge",
                getClientId(client),
                scenario.getCanonicalName()
        };
    }

    @Override
    public void saveChallenge(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId,
            @NonNull C challenge, @NonNull Duration ttl
    ) throws Exception {
        //存储挑战
        challengeCacheManager.set(
                buildNamespace(client, scenario),
                challengeId,
                CHALLENGE_SUFFIX,
                challenge,
                ttl
        );
    }

    @Override
    public void saveChallengeId(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String requestSignature,
            @NonNull String challengeId,
            @NonNull Duration ttl
    ) throws Exception {
        //完成请求特征与挑战id的对应
        challengeIdCacheManager.set(
                buildNamespace(client, scenario),
                requestSignature,
                REQUEST_SIGNATURE_SUFFIX,
                challengeId,
                ttl
        );
    }

    @Nullable
    @Override
    public String getChallengeId(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String requestSignature
    ) {
        return challengeIdCacheManager.get(
                buildNamespace(client, scenario),
                requestSignature,
                REQUEST_SIGNATURE_SUFFIX
        );
    }

    @Nullable
    @Override
    public C loadChallenge(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId
    ) throws Exception {
        return challengeCacheManager.get(
                buildNamespace(client, scenario),
                challengeId,
                CHALLENGE_SUFFIX
        );
    }

    @Override
    public void removeChallenge(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId
    ) throws Exception {
        challengeCacheManager.delete(
                buildNamespace(client, scenario),
                challengeId,
                CHALLENGE_SUFFIX
        );
    }
}
