package io.gardenerframework.camellia.authentication.server.main;

import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.server.main.exception.client.BadOAuth2AuthorizationCodeException;
import io.gardenerframework.camellia.authentication.server.main.exception.client.BadStateException;
import io.gardenerframework.camellia.authentication.server.main.schema.UserAuthenticationRequestToken;
import io.gardenerframework.camellia.authentication.server.main.schema.request.OAuth2AuthorizationCodeParameter;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Validator;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2023/3/6 11:56
 */
public abstract class OAuth2BaseUserAuthenticationService extends AbstractUserAuthenticationService<OAuth2AuthorizationCodeParameter> {
    private final OAuth2BasedIamUserReader oAuth2BasedIamUserReader;
    private final OAuth2StateStore oAuth2StateStore;

    protected OAuth2BaseUserAuthenticationService(@NonNull Validator validator, OAuth2BasedIamUserReader oAuth2BasedIamUserReader, OAuth2StateStore oAuth2StateStore) {
        super(validator);
        this.oAuth2BasedIamUserReader = oAuth2BasedIamUserReader;
        this.oAuth2StateStore = oAuth2StateStore;
    }

    @Override
    protected OAuth2AuthorizationCodeParameter getAuthenticationParameter(@NonNull HttpServletRequest request) {
        return new OAuth2AuthorizationCodeParameter(request);
    }

    @Override
    protected UserAuthenticationRequestToken doConvert(
            @NonNull OAuth2AuthorizationCodeParameter authenticationParameter,
            @Nullable OAuth2RequestingClient client,
            @NonNull Map<String, Object> context
    ) throws Exception {
        //检查state
        if (!oAuth2StateStore.verify(authenticationParameter.getState())) {
            throw new BadStateException(authenticationParameter.getCode());
        }
        //使用授权码去读取用户，并转换为用户的登录名
        Principal principal = oAuth2BasedIamUserReader.readUser(authenticationParameter.getCode());
        if (principal == null) {
            throw new BadOAuth2AuthorizationCodeException(authenticationParameter.getCode());
        }
        return new UserAuthenticationRequestToken(principal);
    }

    @Override
    public void authenticate(
            @NonNull UserAuthenticationRequestToken authenticationRequest,
            @Nullable OAuth2RequestingClient client,
            @NonNull User user,
            @NonNull Map<String, Object> context
    ) throws AuthenticationException {
        //不需要校验
    }

    /**
     * 生成state
     *
     * @return state
     * @throws Exception 发生的问题
     */
    public String createState() throws Exception {
        String state = Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
        oAuth2StateStore.save(state, Duration.ofSeconds(300));
        return state;
    }
}
