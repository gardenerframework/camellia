package io.gardenerframework.camellia.authentication.server.test.mfa;

import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.AuthenticationServerMfaAuthenticatorChallengeRequestFactory;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class serverSideChallengeRequestFactory implements AuthenticationServerMfaAuthenticatorChallengeRequestFactory<ServerSideMfaAuthenticator.ServerSideChallengeRequest> {
    @Nullable
    @Override
    public ServerSideMfaAuthenticator.ServerSideChallengeRequest create(String authenticatorName, @Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull Principal principal, @NonNull User user, @NonNull Map<String, Object> context) {
        if ("server-side".equals(authenticatorName)) {
            return new ServerSideMfaAuthenticator.ServerSideChallengeRequest();
        }
        return null;
    }
}
