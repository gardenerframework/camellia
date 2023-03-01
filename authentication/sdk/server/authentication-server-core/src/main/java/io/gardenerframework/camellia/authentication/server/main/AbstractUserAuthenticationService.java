package io.gardenerframework.camellia.authentication.server.main;

import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.server.main.exception.NestedAuthenticationException;
import io.gardenerframework.camellia.authentication.server.main.schema.UserAuthenticationRequestToken;
import io.gardenerframework.camellia.authentication.server.main.schema.request.AuthenticationRequestParameter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Validator;
import java.util.Map;
import java.util.Objects;

/**
 * @author zhanghan30
 * @date 2023/2/17 12:37
 */
@AllArgsConstructor
public abstract class AbstractUserAuthenticationService<P extends AuthenticationRequestParameter> implements UserAuthenticationService {
    /**
     * 验证器
     */
    @NonNull
    @Getter(AccessLevel.PROTECTED)
    private final Validator validator;

    /**
     * 从请求中获取认证请求参数
     *
     * @param request 请求
     * @return 参数
     */
    protected abstract P getAuthenticationParameter(@NonNull HttpServletRequest request);

    /**
     * 从转换的参数中进行转换
     *
     * @param authenticationParameter 认证参数
     * @param context                 上下文
     * @return 认证请求
     * @throws Exception 在转换过程中出现的问题
     */
    protected abstract UserAuthenticationRequestToken doConvert(
            @NonNull P authenticationParameter,
            @Nullable OAuth2RequestingClient client,
            @NonNull Map<String, Object> context
    ) throws Exception;

    @Override
    public UserAuthenticationRequestToken convert(
            @NonNull HttpServletRequest request,
            @Nullable OAuth2RequestingClient client,
            @NonNull Map<String, Object> context) throws AuthenticationException {
        P authenticationParameter = Objects.requireNonNull(getAuthenticationParameter(request));
        authenticationParameter.validate(validator);
        try {
            return Objects.requireNonNull(doConvert(authenticationParameter, client, context));
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new NestedAuthenticationException(e);
        }
    }
}
