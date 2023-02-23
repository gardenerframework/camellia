package com.jdcloud.gardener.camellia.authorization.test.authentication.main;

import com.jdcloud.gardener.camellia.authorization.authentication.main.UserAuthenticationService;
import com.jdcloud.gardener.camellia.authorization.authentication.main.annotation.AuthenticationType;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.UserAuthenticationRequestToken;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.credentials.BasicCredentials;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
import com.jdcloud.gardener.camellia.authorization.test.security.authentication.principal.AccountExpiredPrincipal;
import com.jdcloud.gardener.camellia.authorization.test.security.authentication.principal.DisabledPrincipal;
import com.jdcloud.gardener.camellia.authorization.test.security.authentication.principal.LockedPrincipal;
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
                        new DisabledPrincipal(username),
                        new BasicCredentials() {
                        }
                );
            case "locked":
                return new UserAuthenticationRequestToken(
                        new LockedPrincipal(username),
                        new BasicCredentials() {
                        }
                );
            case "expired":
                return new UserAuthenticationRequestToken(
                        new AccountExpiredPrincipal(username),
                        new BasicCredentials() {
                        }
                );
            default:
                return new UserAuthenticationRequestToken(
                        null,
                        new BasicCredentials() {
                        }
                );
        }
    }

    @Override
    public void authenticate(UserAuthenticationRequestToken authenticationRequest, User user) throws AuthenticationException {

    }
}
