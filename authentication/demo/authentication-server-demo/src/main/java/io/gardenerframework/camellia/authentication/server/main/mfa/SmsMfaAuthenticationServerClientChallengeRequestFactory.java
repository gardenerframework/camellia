package io.gardenerframework.camellia.authentication.server.main.mfa;

import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeRequest;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.schema.SmsVerificationCodeChallengeRequest;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.AuthenticationServerMfaAuthenticatorChallengeRequestFactory;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Component
public class SmsMfaAuthenticationServerClientChallengeRequestFactory implements AuthenticationServerMfaAuthenticatorChallengeRequestFactory<ChallengeRequest> {
    @Nullable
    @Override
    public ChallengeRequest create(
            String authenticatorName,
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull Principal principal,
            @NonNull User user,
            @NonNull Map<String, Object> context
    ) {
        if ("sms".equals(authenticatorName)) {
            String number = null;
            for (Principal userPrincipal : user.getPrincipals()) {
                if (userPrincipal.getName().length() == 11) {
                    number = userPrincipal.getName();
                }
            }
            if (StringUtils.hasText(number)) {
                return SmsVerificationCodeChallengeRequest.builder().mobilePhoneNumber(number).build();
            }
        }
        return null;
    }
}
