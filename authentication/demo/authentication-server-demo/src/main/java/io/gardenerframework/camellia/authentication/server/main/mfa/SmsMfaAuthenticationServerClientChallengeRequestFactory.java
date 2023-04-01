package io.gardenerframework.camellia.authentication.server.main.mfa;

import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.MfaAuthenticationServerClientChallengeRequest;
import io.gardenerframework.camellia.authentication.server.main.mfa.utils.MfaAuthenticationServerClientChallengeRequestFactory;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.MobilePhoneNumberPrincipal;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SmsMfaAuthenticationServerClientChallengeRequestFactory implements MfaAuthenticationServerClientChallengeRequestFactory {
    @Nullable
    @Override
    public MfaAuthenticationServerClientChallengeRequest create(
            String authenticatorName,
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull Principal principal,
            @NonNull User user,
            @NonNull Map<String, Object> context
    ) {
        if ("sms".equals(authenticatorName)) {
            Map<String, Object> userData = new HashMap<>();
            for (Principal userPrincipal : user.getPrincipals()) {
                if (userPrincipal.getName().length() == 11) {
                    userData.put("mobilePhoneNumber", userPrincipal.getName());
                }
            }
            if (!userData.isEmpty()) {
                return new MfaAuthenticationServerClientChallengeRequest(
                        userData,
                        null
                );
            }
        }
        return null;
    }
}
