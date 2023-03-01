package com.jdcloud.gardener.camellia.authorization.demo.username.recovery;

import com.jdcloud.gardener.camellia.authorization.authentication.main.client.ClientGroupProvider;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;
import com.jdcloud.gardener.camellia.authorization.challenge.WellKnownChallengeType;
import com.jdcloud.gardener.camellia.authorization.challenge.exception.client.InvalidChallengeException;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.Challenge;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.ChallengeRequest;
import com.jdcloud.gardener.camellia.authorization.common.api.security.schema.AccessTokenDetails;
import com.jdcloud.gardener.camellia.authorization.demo.authenticattion.main.user.DemoUserService;
import com.jdcloud.gardener.camellia.authorization.username.recovery.PasswordRecoveryService;
import com.jdcloud.gardener.camellia.authorization.username.recovery.schema.challenge.PasswordRecoveryChallengeRequest;
import com.jdcloud.gardener.camellia.sms.authentication.SmsAuthenticationCodeClient;
import com.jdcloud.gardener.camellia.sms.authentication.SmsAuthenticationCodeEncoder;
import com.jdcloud.gardener.camellia.sms.authentication.SmsAuthenticationCodeStore;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * @author ZhangHan
 * @date 2022/5/15 0:49
 */
@Component
@AllArgsConstructor
@ConditionalOnClass(PasswordRecoveryService.class)
public class DemoPasswordRecoveryService implements PasswordRecoveryService {
    private final SmsAuthenticationCodeStore smsAuthenticationCodeStore;
    private final SmsAuthenticationCodeClient smsAuthenticationCodeClient;
    private final AccessTokenDetails accessTokenDetails;
    private final ClientGroupProvider clientGroupProvider;
    private final SmsAuthenticationCodeEncoder smsAuthenticationCodeEncoder;
    private final DemoUserService userService;
    private final long ttl = 300;

    @Override
    public void resetPassword(String userId, String password) {
        userService.setPassword(password);
    }

    @Nullable
    @Override
    public Challenge sendChallenge(PasswordRecoveryChallengeRequest request) {
        BasicPrincipal principal = null;
        for (BasicPrincipal basicPrincipal : request.getUser().getPrincipals()) {
            principal = basicPrincipal;
            break;
        }
        String phoneNumber = Objects.requireNonNull(principal).getName();
        String code = String.format("%06d", new SecureRandom().nextInt(999999 + 1));
        String encoded = smsAuthenticationCodeEncoder.encode(
                request.getClientGroup(),
                code,
                DemoPasswordRecoveryService.class.getCanonicalName()
        );
        String challengeId = UUID.randomUUID().toString();
        smsAuthenticationCodeClient.sendCode(request.getHeaders(), request.getClientGroup(), phoneNumber, code, DemoPasswordRecoveryService.class.getCanonicalName());
        //再保存挑战
        smsAuthenticationCodeStore.saveCode(request.getClientGroup(), challengeId, encoded, DemoPasswordRecoveryService.class.getCanonicalName(), Duration.ofSeconds(ttl));
        return new Challenge(challengeId, WellKnownChallengeType.SMS, Date.from(Instant.now().plus(Duration.ofSeconds(ttl))), null);
    }

    @Override
    public boolean validateResponse(String id, String response) throws InvalidChallengeException {
        String clientGroup = clientGroupProvider.getClientGroup(accessTokenDetails.getRegisteredClient());
        String code = smsAuthenticationCodeStore.getCode(clientGroup, id, DemoPasswordRecoveryService.class.getCanonicalName());
        return Objects.equals(code, smsAuthenticationCodeEncoder.encode(clientGroup, response, DemoPasswordRecoveryService.class.getCanonicalName()));
    }

    @Override
    public void closeChallenge(String id) {
        String clientGroup = clientGroupProvider.getClientGroup(accessTokenDetails.getRegisteredClient());
        smsAuthenticationCodeStore.removeCode(clientGroup, id, DemoPasswordRecoveryService.class.getCanonicalName());
    }

    @Override
    public String getCooldownKey(PasswordRecoveryChallengeRequest request) {
        return String.format("%s.%s", Objects.requireNonNull(request.getUser()).getId(), request.getClientGroup());
    }

    @Override
    public long getCooldown() {
        return 60;
    }
}
