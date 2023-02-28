package com.jdcloud.gardener.camellia.authorization.test.authentication.main;

import com.jdcloud.gardener.camellia.authorization.test.security.authentication.principal.AccountExpiredPrincipal;
import com.jdcloud.gardener.camellia.authorization.test.security.authentication.principal.DisabledPrincipal;
import com.jdcloud.gardener.camellia.authorization.test.security.authentication.principal.LockedPrincipal;
import io.gardenerframework.camellia.authentication.server.main.UserAuthenticationService;
import io.gardenerframework.camellia.authentication.server.main.annotation.AuthenticationType;
import io.gardenerframework.camellia.authentication.server.main.schema.UserAuthenticationRequestToken;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.credentials.EmptyCredentials;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ZhangHan
 * @date 2022/5/13 11:21
 */
@AuthenticationType("AccountStatusErrorRequest")
@Component
public class AccountStatusErrorRequest implements UserAuthenticationService {
    @Override
    public UserAuthenticationRequestToken convert(HttpServletRequest request) throws AuthenticationException {
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
    public void authenticate(UserAuthenticationRequestToken authenticationRequest, User user) throws AuthenticationException {

    }
}
