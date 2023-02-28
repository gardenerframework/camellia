package io.gardenerframework.camellia.authentication.server.test.authentication.main;

import io.gardenerframework.camellia.authentication.server.main.UserAuthenticationService;
import io.gardenerframework.camellia.authentication.server.main.annotation.AuthenticationType;
import io.gardenerframework.camellia.authentication.server.main.schema.UserAuthenticationRequestToken;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ZhangHan
 * @date 2022/5/13 11:21
 */
@AuthenticationType("NullAuthenticationRequest")
@Component
public class NullAuthenticationRequest implements UserAuthenticationService {
    @Override
    public UserAuthenticationRequestToken convert(HttpServletRequest request) throws AuthenticationException {
        return null;
    }

    @Override
    public void authenticate(UserAuthenticationRequestToken authenticationRequest, User user) throws AuthenticationException {

    }
}
