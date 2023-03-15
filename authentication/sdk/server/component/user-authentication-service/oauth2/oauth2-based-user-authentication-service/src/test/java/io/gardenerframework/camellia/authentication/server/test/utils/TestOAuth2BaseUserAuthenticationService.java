package io.gardenerframework.camellia.authentication.server.test.utils;

import io.gardenerframework.camellia.authentication.server.main.OAuth2BasedUserAuthenticationService;
import io.gardenerframework.camellia.authentication.server.main.OAuth2StateStore;
import io.gardenerframework.camellia.authentication.server.main.annotation.AuthenticationType;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.UsernamePrincipal;
import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.validation.Validator;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2023/3/6 19:09
 */
@Component
@AuthenticationType("test")
public class TestOAuth2BaseUserAuthenticationService extends OAuth2BasedUserAuthenticationService {
    public TestOAuth2BaseUserAuthenticationService(@NonNull Validator validator, OAuth2StateStore oAuth2StateStore) {
        super(validator, oAuth2StateStore);
    }

    @Override
    protected AccessToken obtainAccessToken(@NonNull String authorizationCode, @NonNull Map<String, Object> context) throws Exception {
        return AccessToken.builder().accessToken(authorizationCode).build();
    }

    @Nullable
    @Override
    protected Principal getPrincipal(@NonNull AccessToken accessToken, @NonNull Map<String, Object> context) throws Exception {
        return UsernamePrincipal.builder().name(accessToken.getAccessToken()).build();
    }
}
