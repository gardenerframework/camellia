package io.gardenerframework.camellia.authentication.server.test.authentication.main;

import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.server.main.UserAuthenticationService;
import io.gardenerframework.camellia.authentication.server.main.annotation.AuthenticationType;
import io.gardenerframework.camellia.authentication.server.main.schema.UserAuthenticationRequestToken;
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
@AuthenticationType("NullAuthenticationRequest")
@Component
public class NullAuthenticationRequest implements UserAuthenticationService {
    @Override
    public UserAuthenticationRequestToken convert(
            @NonNull HttpServletRequest request,
            @Nullable OAuth2RequestingClient client,
            @NonNull Map<String, Object> context
    ) throws AuthenticationException {
        return null;
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
