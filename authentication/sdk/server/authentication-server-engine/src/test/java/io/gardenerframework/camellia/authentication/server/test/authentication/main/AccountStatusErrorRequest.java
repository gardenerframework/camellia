package io.gardenerframework.camellia.authentication.server.test.authentication.main;

import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.server.test.security.authentication.principal.AccountExpiredPrincipal;
import io.gardenerframework.camellia.authentication.server.test.security.authentication.principal.DisabledPrincipal;
import io.gardenerframework.camellia.authentication.server.test.security.authentication.principal.LockedPrincipal;
import io.gardenerframework.camellia.authentication.server.main.UserAuthenticationService;
import io.gardenerframework.camellia.authentication.server.main.annotation.AuthenticationType;
import io.gardenerframework.camellia.authentication.server.main.schema.UserAuthenticationRequestToken;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.credentials.EmptyCredentials;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author ZhangHan
 * @date 2022/5/13 11:21
 */
@AuthenticationType("AccountStatusErrorRequest")
@Component
public class AccountStatusErrorRequest implements UserAuthenticationService {
    @Override
    public UserAuthenticationRequestToken convert(
            @NonNull HttpServletRequest request,
            @Nullable OAuth2RequestingClient client,
            @NonNull Map<String, Object> context
    ) throws AuthenticationException {
        String username = request.getParameter("username");
        switch (username) {
            case "disabled":
                return new UserAuthenticationRequestToken(
                        DisabledPrincipal.builder().name(username).build(),
                        new EmptyCredentials()
                );
            case "locked":
                return new UserAuthenticationRequestToken(
                        LockedPrincipal.builder().name(username).build(),
                        new EmptyCredentials()
                );
            case "expired":
                return new UserAuthenticationRequestToken(
                        AccountExpiredPrincipal.builder().name(username).build(),
                        new EmptyCredentials()
                );
            default:
                return new UserAuthenticationRequestToken(
                        null,
                        new EmptyCredentials()
                );
        }
    }

    @Override
    public void authenticate(
            @NonNull UserAuthenticationRequestToken authenticationRequest,
            @Nullable OAuth2RequestingClient client,
            @NonNull User user,
            @NonNull Map<String, Object> context
    ) throws AuthenticationException {

    }
}
