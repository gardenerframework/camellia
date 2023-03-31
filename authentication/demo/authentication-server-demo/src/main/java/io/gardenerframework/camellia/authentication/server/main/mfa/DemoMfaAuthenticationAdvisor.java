package io.gardenerframework.camellia.authentication.server.main.mfa;

import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.server.main.mfa.advisor.AuthenticationServerMfaAuthenticatorAdvisor;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.MobilePhoneNumberPrincipal;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Component
public class DemoMfaAuthenticationAdvisor implements AuthenticationServerMfaAuthenticatorAdvisor {
    @Nullable
    @Override
    public String getAuthenticator(
            @NonNull HttpServletRequest request,
            @Nullable OAuth2RequestingClient client,
            @NonNull String authenticationType,
            @NonNull User user,
            @NonNull Map<String, Object> context
    ) throws Exception {
        for (Principal principal : user.getPrincipals()) {
            if (principal instanceof MobilePhoneNumberPrincipal) {
                return "sms";
            }
        }
        return null;
    }
}
