package io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.demo.challenge;

import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeCooldownManager;
import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.support.GenericCachedChallengeContextStore;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.support.GenericCachedChallengeStore;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.MfaAuthenticator;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.AbstractSmsVerificationCodeChallengeResponseService;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.client.SmsVerificationCodeClient;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.schema.SmsVerificationCodeChallengeContext;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.schema.SmsVerificationCodeChallengeRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
public class SmsDemoMfaAuthenticator extends AbstractSmsVerificationCodeChallengeResponseService<SmsVerificationCodeChallengeRequest, Challenge, SmsVerificationCodeChallengeContext> implements
        MfaAuthenticator<SmsVerificationCodeChallengeRequest, Challenge, SmsVerificationCodeChallengeContext> {

    protected SmsDemoMfaAuthenticator(@NonNull GenericCachedChallengeStore challengeStore, @NonNull ChallengeCooldownManager challengeCooldownManager, @NonNull GenericCachedChallengeContextStore challengeContextStore, @NonNull SmsVerificationCodeClient smsVerificationCodeClient) {
        super(challengeStore, challengeCooldownManager, challengeContextStore.migrateType(), smsVerificationCodeClient);
    }


    @Override
    protected Challenge createSmsVerificationChallenge(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull SmsVerificationCodeChallengeRequest request, @NonNull Map<String, Object> payload) {
        return Challenge.builder()
                .id(UUID.randomUUID().toString())
                .expiryTime(Date.from(Instant.now().plus(Duration.ofMinutes(5))))
                .build();
    }

    @Override
    protected @NonNull SmsVerificationCodeChallengeContext createSmsVerificationChallengeContext(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull SmsVerificationCodeChallengeRequest request, @NonNull Challenge challenge, @NonNull Map<String, Object> payload) {
        return new SmsVerificationCodeChallengeContext();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class MobilePhoneNumber {
        @NotBlank
        private String mobilePhoneNumber;
    }
}
