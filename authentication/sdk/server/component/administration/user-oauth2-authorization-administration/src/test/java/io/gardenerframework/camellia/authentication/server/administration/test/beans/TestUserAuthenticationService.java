package io.gardenerframework.camellia.authentication.server.administration.test.beans;

import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.server.main.UserAuthenticationService;
import io.gardenerframework.camellia.authentication.server.main.annotation.AuthenticationType;
import io.gardenerframework.camellia.authentication.server.main.schema.UserAuthenticationRequestToken;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.UsernamePrincipal;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2023/3/27 12:36
 */
@AuthenticationType("test")
@Component
public class TestUserAuthenticationService implements UserAuthenticationService {
    @Override
    public UserAuthenticationRequestToken convert(@NonNull HttpServletRequest request, @Nullable OAuth2RequestingClient client, @NonNull Map<String, Object> context) throws AuthenticationException {
        return new UserAuthenticationRequestToken(UsernamePrincipal.builder().name(request.getParameter("username")).build());
    }

    @Override
    public void authenticate(@NonNull UserAuthenticationRequestToken authenticationRequest, @Nullable OAuth2RequestingClient client, @NonNull User user, @NonNull Map<String, Object> context) throws AuthenticationException {

    }
}
