package io.gardenerframework.camellia.authentication.server.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.server.main.annotation.AuthenticationType;
import io.gardenerframework.camellia.authentication.server.main.exception.client.BadOAuth2AuthorizationCodeException;
import io.gardenerframework.camellia.authentication.server.main.exception.client.BadStateException;
import io.gardenerframework.camellia.authentication.server.main.schema.UserAuthenticationRequestToken;
import io.gardenerframework.camellia.authentication.server.main.schema.request.OAuth2AuthorizationCodeParameter;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Validator;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2023/3/6 11:56
 */
public abstract class OAuth2BasedUserAuthenticationService extends AbstractUserAuthenticationService<OAuth2AuthorizationCodeParameter> {
    private final OAuth2StateStore oAuth2StateStore;

    protected OAuth2BasedUserAuthenticationService(@NonNull Validator validator, OAuth2StateStore oAuth2StateStore) {
        super(validator);
        this.oAuth2StateStore = oAuth2StateStore;
    }

    @Override
    protected OAuth2AuthorizationCodeParameter getAuthenticationParameter(@NonNull HttpServletRequest request) {
        return new OAuth2AuthorizationCodeParameter(request);
    }

    /**
     * 获取access token
     *
     * @param authorizationCode 授权码
     * @param context           上下文
     * @return access token
     * @throws Exception 发生问题
     */
    protected abstract AccessToken obtainAccessToken(@NonNull String authorizationCode, @NonNull Map<String, Object> context)
            throws Exception;

    /**
     * 从iam读取用户并转换为登录名
     *
     * @param accessToken access token
     * @param context     上下文
     * @return 登录名
     * @throws Exception 发生问题
     */
    @Nullable
    protected abstract Principal getPrincipal(@NonNull AccessToken accessToken, @NonNull Map<String, Object> context)
            throws Exception;

    @Override
    protected UserAuthenticationRequestToken doConvert(
            @NonNull OAuth2AuthorizationCodeParameter authenticationParameter,
            @Nullable OAuth2RequestingClient client,
            @NonNull Map<String, Object> context
    ) throws Exception {
        //检查state
        if (!oAuth2StateStore.verify(this.getClass(), authenticationParameter.getState())) {
            throw new BadStateException(authenticationParameter.getCode());
        }
        AccessToken accessToken = obtainAccessToken(authenticationParameter.getCode(), context);
        //存储access token
        context.put(this.getClass().getName(), accessToken);
        //使用授权码去读取用户，并转换为用户的登录名
        Principal principal = getPrincipal(accessToken, context);
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
        Map<String, Object> stateHolder = new HashMap<>();
        stateHolder.put("state", UUID.randomUUID().toString());
        AuthenticationType annotation = AnnotationUtils.findAnnotation(this.getClass(), AuthenticationType.class);
        if (annotation != null) {
            //表达这是当前登录方式的state
            stateHolder.put(annotation.value(), true);
        }
        String state = Base64.getEncoder().encodeToString(new ObjectMapper().writeValueAsBytes(stateHolder));
        oAuth2StateStore.save(this.getClass(), state, Duration.ofSeconds(300));
        return state;
    }

    /**
     * 从上下文中获取 access token
     *
     * @param context 上下文
     * @return 访问令牌
     */
    @Nullable
    public AccessToken getAccessTokenFromContext(@NonNull Map<String, Object> context) {
        return (AccessToken) context.get(this.getClass().getName());
    }

    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    public static class AccessToken {
        @NonNull
        private String accessToken;

        @Nullable
        private String refreshToken;

        private long expireIn;

        @Nullable
        private String scope;
    }
}
