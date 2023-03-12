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
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.SmsMfaAuthenticationChallengeContext;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.SmsMfaAuthenticationChallengeRequest;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.MobilePhoneNumberPrincipal;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class SmsMfaAuthenticationChallengeResponseService extends
        AbstractSmsVerificationCodeChallengeResponseService<SmsMfaAuthenticationChallengeRequest, Challenge, SmsMfaAuthenticationChallengeContext>
        implements MfaAuthenticationChallengeResponseService<SmsMfaAuthenticationChallengeRequest, SmsMfaAuthenticationChallengeContext> {
    protected SmsMfaAuthenticationChallengeResponseService(@NonNull GenericCachedChallengeStore challengeStore, @NonNull ChallengeCooldownManager challengeCooldownManager, @NonNull GenericCachedChallengeContextStore challengeContextStore, @NonNull SmsVerificationCodeClient smsVerificationCodeClient) {
        super(challengeStore, challengeCooldownManager, challengeContextStore.migrateType(), smsVerificationCodeClient);
    }

    @Override
    protected Challenge createSmsVerificationChallenge(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull SmsMfaAuthenticationChallengeRequest request, @NonNull Map<String, Object> payload) {
        return Challenge.builder()
                .id(UUID.randomUUID().toString())
                //todo 改成可配置
                .expiryTime(Date.from(Instant.now().plus(Duration.ofSeconds(300))))
                .build();
    }

    @Override
    protected @NonNull SmsMfaAuthenticationChallengeContext createSmsVerificationChallengeContext(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull SmsMfaAuthenticationChallengeRequest request, @NonNull Challenge challenge, @NonNull Map<String, Object> payload) {
        SmsMfaAuthenticationChallengeContext smsMfaAuthenticationChallengeContext = new SmsMfaAuthenticationChallengeContext();
        smsMfaAuthenticationChallengeContext.setUser(request.getUser());
        smsMfaAuthenticationChallengeContext.setPrincipal(request.getPrincipal());
        smsMfaAuthenticationChallengeContext.setClient((OAuth2RequestingClient) client);
        return smsMfaAuthenticationChallengeContext;
    }

    @Override
    public Challenge sendChallenge(@Nullable OAuth2RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull Principal principal, @NonNull User user, @NonNull Map<String, Object> context) throws Exception {
        SmsMfaAuthenticationChallengeRequest smsMfaAuthenticationChallengeRequest = new SmsMfaAuthenticationChallengeRequest();
        smsMfaAuthenticationChallengeRequest.setContext(context);
        smsMfaAuthenticationChallengeRequest.setUser(user);
        smsMfaAuthenticationChallengeRequest.setPrincipal(principal);
        Collection<@NonNull Principal> principals = user.getPrincipals();
        if (!CollectionUtils.isEmpty(principals)) {
            for (Principal principalInUser : principals) {
                if (principalInUser instanceof MobilePhoneNumberPrincipal) {
                    smsMfaAuthenticationChallengeRequest.setMobilePhoneNumber(principalInUser.getName());
                }
            }
        }
        if (!StringUtils.hasText(smsMfaAuthenticationChallengeRequest.getMobilePhoneNumber())) {
            //手机号都没有，发什么挑战？
            throw new UnsupportedOperationException(user.getId() + " does not have a mobile phone.");
        }
        return sendChallenge(client, scenario, smsMfaAuthenticationChallengeRequest);
    }
}
