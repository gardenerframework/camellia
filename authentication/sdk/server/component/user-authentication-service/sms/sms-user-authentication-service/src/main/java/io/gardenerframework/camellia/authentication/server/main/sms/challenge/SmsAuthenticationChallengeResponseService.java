package io.gardenerframework.camellia.authentication.server.main.sms.challenge;

import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeCooldownManager;
import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.support.GenericCachedChallengeContextStore;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.AbstractSmsVerificationCodeChallengeResponseService;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.client.SmsVerificationCodeClient;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.schema.SmsVerificationCodeChallengeContext;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.schema.SmsVerificationCodeChallengeRequest;
import io.gardenerframework.camellia.authentication.server.configuration.SmsAuthenticationOption;
import io.gardenerframework.camellia.authentication.server.configuration.SmsAuthenticationServiceComponent;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@SmsAuthenticationServiceComponent
public class SmsAuthenticationChallengeResponseService extends AbstractSmsVerificationCodeChallengeResponseService<
        SmsVerificationCodeChallengeRequest,
        Challenge,
        SmsVerificationCodeChallengeContext> {
    @NonNull
    private final SmsAuthenticationOption option;

    protected SmsAuthenticationChallengeResponseService(
            @NonNull ChallengeCooldownManager challengeCooldownManager,
            @NonNull GenericCachedChallengeContextStore challengeContextStore,
            @NonNull SmsVerificationCodeClient smsVerificationCodeClient,
            @NonNull SmsAuthenticationOption option
    ) {
        super(challengeCooldownManager, challengeContextStore.migrateType(), smsVerificationCodeClient);
        this.option = option;
    }

    @Override
    protected int getCooldownTime(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull SmsVerificationCodeChallengeRequest request) {
        return option.getVerificationCodeCooldown();
    }

    @Override
    protected Challenge createSmsVerificationChallenge(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull SmsVerificationCodeChallengeRequest request,
            @NonNull Map<String, Object> payload
    ) {
        return Challenge.builder()
                //手机号就是挑战id
                .id(request.getMobilePhoneNumber())
                .expiryTime(Date.from(Instant.now().plus(Duration.ofSeconds(option.getVerificationCodeTtl()))))
                .build();
    }

    @Override
    protected @NonNull SmsVerificationCodeChallengeContext createSmsVerificationChallengeContext(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull SmsVerificationCodeChallengeRequest request,
            @NonNull Challenge challenge,
            @NonNull Map<String, Object> payload
    ) {
        //验证码外面会搞定
        //fix 不填会报错
        return new SmsVerificationCodeChallengeContext();
    }
}
