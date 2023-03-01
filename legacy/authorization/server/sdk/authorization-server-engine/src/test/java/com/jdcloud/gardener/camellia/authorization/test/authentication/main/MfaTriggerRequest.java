package com.jdcloud.gardener.camellia.authorization.test.authentication.main;

import com.jdcloud.gardener.camellia.authorization.authentication.main.UserAuthenticationService;
import com.jdcloud.gardener.camellia.authorization.authentication.main.annotation.AuthenticationType;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.UserAuthenticationRequestToken;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.credentials.PasswordCredentials;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.UsernamePrincipal;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
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
        return new UserAuthenticationRequestToken(
                new UsernamePrincipal(username),
                new PasswordCredentials(password)
        );
    }

    @Override
    public void authenticate(UserAuthenticationRequestToken authenticationRequest, User user) throws AuthenticationException {

    }
}
