package io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.test.bean;

import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.challenge.core.annotation.ChallengeAuthenticator;
import io.gardenerframework.camellia.authentication.infra.challenge.core.exception.ChallengeInCooldownException;
import io.gardenerframework.camellia.authentication.infra.challenge.core.exception.ChallengeResponseServiceException;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.MfaAuthenticator;
import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2023/3/29 18:09
 */
@Component
@ChallengeAuthenticator("test")
public class TestMfaAuthenticator implements MfaAuthenticator<
        TestChallengeRequest, TestChallenge, TestChallengeContext> {
    @Override
    public TestChallengeRequest createChallengeRequest(@NonNull Map<String, Object> userData, @Nullable RequestingClient client, Class<? extends Scenario> scenario) {
        return new TestChallengeRequest();
    }

    @Override
    public TestChallenge sendChallenge(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull TestChallengeRequest request) throws ChallengeResponseServiceException, ChallengeInCooldownException {
        return TestChallenge.builder()
                .id(UUID.randomUUID().toString())
                .expiryTime(Date.from(Instant.now().plus(Duration.ofSeconds(5))))
                .field(scenario.getName())
                .build();
    }

    @Override
    public boolean verifyResponse(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull String challengeId, @NonNull String response) throws ChallengeResponseServiceException {
        return true;
    }

    @Nullable
    @Override
    public TestChallengeContext getContext(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull String challengeId) throws ChallengeResponseServiceException {
        return new TestChallengeContext();
    }

    @Override
    public void closeChallenge(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull String challengeId) throws ChallengeResponseServiceException {

    }
}
