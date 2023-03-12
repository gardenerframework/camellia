package io.gardenerframework.camellia.authentication.server.main.mfa.challenge;

import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeCooldownManager;
import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.support.GenericCachedChallengeContextStore;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.support.GenericCachedChallengeStore;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.AbstractSmsVerificationCodeChallengeResponseService;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.client.SmsVerificationCodeClient;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.SmsMfaChallengeContext;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.SmsMfaChallengeRequest;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class SmsMfaAuthenticationChallengeResponseService extends
        AbstractSmsVerificationCodeChallengeResponseService<SmsMfaChallengeRequest, Challenge, SmsMfaChallengeContext>
        implements MfaAuthenticationChallengeResponseService<SmsMfaChallengeRequest, SmsMfaChallengeContext> {
    protected SmsMfaAuthenticationChallengeResponseService(@NonNull GenericCachedChallengeStore challengeStore, @NonNull ChallengeCooldownManager challengeCooldownManager, @NonNull GenericCachedChallengeContextStore challengeContextStore, @NonNull SmsVerificationCodeClient smsVerificationCodeClient) {
        super(challengeStore, challengeCooldownManager, challengeContextStore.migrateType(), smsVerificationCodeClient);
    }

    @Override
    public SmsMfaChallengeRequest createRequest(@NonNull Principal principal, @NonNull User user, @NonNull Map<String, Object> context) {
        return SmsMfaChallengeRequest.builder()
                .principal(principal)
                .user(user)
                .context(context)
                .build();
    }

    @Override
    protected Challenge createSmsVerificationChallenge(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull SmsMfaChallengeRequest request, @NonNull Map<String, Object> payload) {
        return Challenge.builder()
                .id(UUID.randomUUID().toString())
                //todo 改成可配置
                .expiryTime(Date.from(Instant.now().plus(Duration.ofSeconds(300))))
                .build();
    }

    @Override
    protected @NonNull SmsMfaChallengeContext createSmsVerificationChallengeContext(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull SmsMfaChallengeRequest request, @NonNull Challenge challenge, @NonNull Map<String, Object> payload) {
        return SmsMfaChallengeContext.builder()
                .user(request.getUser())
                .principal(request.getPrincipal())
                .client((OAuth2RequestingClient) client)
                .code("")
                .build();
    }
}
