package io.gardenerframework.camellia.authentication.infra.sms.challenge.test.beans;

import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeCooldownManager;
import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.support.GenericCachedChallengeContextStore;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.AbstractSmsVerificationCodeChallengeResponseService;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.schema.SmsVerificationCodeChallengeContext;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.schema.SmsVerificationCodeChallengeRequest;
import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * @author chris
 * <p>
 * date: 2023/4/2
 */
@Component
public class TestSmsVerificationCodeChallengeResponseService extends AbstractSmsVerificationCodeChallengeResponseService<SmsVerificationCodeChallengeRequest, Challenge, SmsVerificationCodeChallengeContext> {
    protected TestSmsVerificationCodeChallengeResponseService(@NonNull ChallengeCooldownManager challengeCooldownManager, @NonNull GenericCachedChallengeContextStore challengeContextStore, TestSmsVerificationCodeClient client) {
        super(challengeCooldownManager, challengeContextStore.migrateType(), client);
    }

    @Override
    protected Challenge createSmsVerificationChallenge(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull SmsVerificationCodeChallengeRequest request, @NonNull Map<String, Object> payload) {
        return Challenge.builder()
                .id(UUID.randomUUID().toString())
                .expiryTime(Date.from(Instant.now().plus(Duration.ofSeconds(10))))
                .build();
    }

    @Override
    protected @NonNull SmsVerificationCodeChallengeContext createSmsVerificationChallengeContext(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull SmsVerificationCodeChallengeRequest request, @NonNull Challenge challenge, @NonNull Map<String, Object> payload) {
        return new SmsVerificationCodeChallengeContext();
    }

}
