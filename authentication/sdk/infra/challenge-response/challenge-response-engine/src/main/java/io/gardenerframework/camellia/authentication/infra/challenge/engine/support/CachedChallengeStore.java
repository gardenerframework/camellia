package io.gardenerframework.camellia.authentication.infra.challenge.engine.support;

import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeStore;
import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
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
public class CachedChallengeStore implements ChallengeStore {
    private static final String REQUEST_SIGNATURE_SUFFIX = "requestSignature";
    private static final String CHALLENGE_SUFFIX = "challenge";
    private static final String CHALLENGE_VERIFIED_FLAG_SUFFIX = "flag";
    @NonNull
    private final BasicCacheManager<Challenge> challengeCacheManager;
    @NonNull
    private final BasicCacheManager<String> challengeIdCacheManager;
    @NonNull
    private final BasicCacheManager<Boolean> challengeVerifiedFlagCacheManager;

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
                "challenge",
                applicationId,
                scenario.getCanonicalName()
        };
    }

    @Override
    public void saveChallenge(
            @NonNull String applicationId,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String requestSignature,
            @NonNull Challenge challenge, @NonNull Duration ttl
    ) throws Exception {
        //获取挑战id
        String challengeId = challenge.getId();
        //完成请求特征与挑战id的对应
        challengeIdCacheManager.set(
                buildNamespace(applicationId, scenario),
                requestSignature,
                REQUEST_SIGNATURE_SUFFIX,
                challengeId,
                ttl
        );
        //存储挑战
        challengeCacheManager.set(
                buildNamespace(applicationId, scenario),
                challengeId,
                CHALLENGE_SUFFIX,
                challenge,
                ttl
        );
    }

    @Nullable
    @Override
    public String getChallengeId(
            @NonNull String applicationId,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String requestSignature
    ) {
        return challengeIdCacheManager.get(
                buildNamespace(applicationId, scenario),
                requestSignature,
                REQUEST_SIGNATURE_SUFFIX
        );
    }

    @Nullable
    @Override
    public Challenge loadChallenge(
            @NonNull String applicationId,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId
    ) throws Exception {
        return challengeCacheManager.get(
                buildNamespace(applicationId, scenario),
                challengeId,
                CHALLENGE_SUFFIX
        );
    }

    @Override
    public void updateChallengeVerifiedFlag(
            @NonNull String applicationId,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId,
            boolean verified,
            @NonNull Duration ttl
    ) throws Exception {
        challengeVerifiedFlagCacheManager.set(
                buildNamespace(applicationId, scenario),
                challengeId,
                CHALLENGE_VERIFIED_FLAG_SUFFIX,
                verified,
                ttl
        );
    }

    @Override
    public boolean isChallengeVerified(
            @NonNull String applicationId,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId
    ) throws Exception {
        return Boolean.TRUE.equals(
                challengeVerifiedFlagCacheManager.get(
                        buildNamespace(applicationId, scenario),
                        challengeId,
                        CHALLENGE_VERIFIED_FLAG_SUFFIX
                )
        );
    }

    @Override
    public void removeChallenge(
            @NonNull String applicationId,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId
    ) throws Exception {
        challengeCacheManager.delete(
                buildNamespace(applicationId, scenario),
                challengeId,
                CHALLENGE_SUFFIX
        );
        challengeVerifiedFlagCacheManager.delete(
                buildNamespace(applicationId, scenario),
                challengeId,
                CHALLENGE_VERIFIED_FLAG_SUFFIX
        );
    }
}
