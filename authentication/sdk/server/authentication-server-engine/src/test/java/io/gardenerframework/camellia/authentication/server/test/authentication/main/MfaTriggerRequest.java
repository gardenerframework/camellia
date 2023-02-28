package io.gardenerframework.camellia.authentication.server.test.authentication.main;

import io.gardenerframework.camellia.authentication.server.main.UserAuthenticationService;
import io.gardenerframework.camellia.authentication.server.main.annotation.AuthenticationType;
import io.gardenerframework.camellia.authentication.server.main.schema.UserAuthenticationRequestToken;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.credentials.PasswordCredentials;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.UsernamePrincipal;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ZhangHan
 * @date 2022/5/13 11:21
 */
@AuthenticationType("MfaTriggerRequest")
@Component
public class MfaTriggerRequest implements UserAuthenticationService {
    @Override
    public UserAuthenticationRequestToken convert(HttpServletRequest request) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        return
                new UserAuthenticationRequestToken(
                        UsernamePrincipal.builder().name(username).build(),
                        PasswordCredentials.builder().password(password).build()
                );
    }

    @Override
    public void authenticate(UserAuthenticationRequestToken authenticationRequest, User user) throws AuthenticationException {

    }
}
