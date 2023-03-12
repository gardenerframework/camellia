package io.gardenerframework.camellia.authentication.server.main.sms.challenge;

import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeCooldownManager;
import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.support.GenericCachedChallengeContextStore;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.support.GenericCachedChallengeStore;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.AbstractSmsVerificationCodeChallengeResponseService;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.client.SmsVerificationCodeClient;
import io.gardenerframework.camellia.authentication.server.configuration.SmsAuthenticationOption;
import io.gardenerframework.camellia.authentication.server.configuration.SmsAuthenticationServiceComponent;
import io.gardenerframework.camellia.authentication.server.main.sms.challenge.schema.SmsAuthenticationChallengeContext;
import io.gardenerframework.camellia.authentication.server.main.sms.challenge.schema.SmsAuthenticationChallengeRequest;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@SmsAuthenticationServiceComponent
public class SmsAuthenticationChallengeResponseService extends AbstractSmsVerificationCodeChallengeResponseService<
        SmsAuthenticationChallengeRequest,
        Challenge,
        SmsAuthenticationChallengeContext> {
    @NonNull
    private final SmsAuthenticationOption option;

    protected SmsAuthenticationChallengeResponseService(
            @NonNull GenericCachedChallengeStore challengeStore,
            @NonNull ChallengeCooldownManager challengeCooldownManager,
            @NonNull GenericCachedChallengeContextStore challengeContextStore,
            @NonNull SmsVerificationCodeClient smsVerificationCodeClient,
            @NonNull SmsAuthenticationOption option
    ) {
        super(challengeStore, challengeCooldownManager, challengeContextStore.migrateType(), smsVerificationCodeClient);
        this.option = option;
    }

    @Override
    protected int getCooldownTime(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull SmsAuthenticationChallengeRequest request) {
        return option.getVerificationCodeCooldown();
    }

    @Override
    protected Challenge createSmsVerificationChallenge(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull SmsAuthenticationChallengeRequest request,
            @NonNull Map<String, Object> payload
    ) {
        return Challenge.builder()
                //手机号就是挑战id
                .id(request.getMobilePhoneNumber())
                .expiryTime(Date.from(Instant.now().plus(Duration.ofSeconds(option.getVerificationCodeTtl()))))
                .build();
    }

    @Override
    protected @NonNull SmsAuthenticationChallengeContext createSmsVerificationChallengeContext(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull SmsAuthenticationChallengeRequest request,
            @NonNull Challenge challenge,
            @NonNull Map<String, Object> payload
    ) {
        //验证码外面会搞定
        //fix 不填会报错
        return SmsAuthenticationChallengeContext.builder().code("")
                .build();
    }
}
