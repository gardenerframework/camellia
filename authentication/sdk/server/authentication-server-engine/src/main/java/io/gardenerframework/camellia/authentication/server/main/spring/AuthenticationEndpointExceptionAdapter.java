package io.gardenerframework.camellia.authentication.server.main.spring;

import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEngineComponent;
import io.gardenerframework.camellia.authentication.server.main.exception.NestedAuthenticationException;
import io.gardenerframework.camellia.authentication.server.main.exception.OAuth2ErrorCodes;
import io.gardenerframework.camellia.authentication.server.main.exception.annotation.OAuth2ErrorCode;
import io.gardenerframework.camellia.authentication.server.main.utils.AuthenticationEndpointMatcher;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 负责在token end point 将捕捉到的一般的{@link AuthenticationException}转为{@link OAuth2AuthenticationException}
 * <p>
 * 如果不是那没什么好转换的
 * <p>
 * 因此它适配多种认证端点，所以叫适配器
 *
 * @author zhanghan30
 * @date 2022/4/21 7:33 下午
 */
@NoArgsConstructor
@AuthenticationServerEngineComponent
public class AuthenticationEndpointExceptionAdapter {
    private static final Map<Class<? extends Exception>, String> OAUTH2_ERROR_CODE_REGISTRY = new ConcurrentHashMap<>();


    static {
        OAUTH2_ERROR_CODE_REGISTRY.put(BadCredentialsException.class, OAuth2ErrorCodes.UNAUTHORIZED);
        OAUTH2_ERROR_CODE_REGISTRY.put(AccountStatusException.class, OAuth2ErrorCodes.UNAUTHORIZED);
        OAUTH2_ERROR_CODE_REGISTRY.put(AuthenticationServiceException.class, OAuth2ErrorCodes.SERVER_ERROR);
    }

    @Setter(value = AccessLevel.PRIVATE, onMethod = @__({@Autowired}))
    private AuthenticationEndpointMatcher authenticationEndpointMatcher;

    /**
     * 尝试在注册表中查找
     *
     * @param exception 异常类
     * @return 对应的错误码
     */
    @Nullable
    private static String lookupErrorCodeRegistry(Exception exception) {
        Class<? extends Exception> clazz = exception.getClass();
        for (Map.Entry<Class<? extends Exception>, String> entry : OAUTH2_ERROR_CODE_REGISTRY.entrySet()) {
            Class<? extends Exception> k = entry.getKey();
            String v = entry.getValue();
            if (k.isAssignableFrom(clazz)) {
                return v;
            }
        }
        return null;
    }

    /**
     * 进行适配工作
     *
     * @param request   http 请求
     * @param exception 捕捉到的认证异常
     * @return 适配后的认证异常
     */
    public AuthenticationException adapt(HttpServletRequest request, AuthenticationException exception) {
        if (authenticationEndpointMatcher.isTokenEndpoint(request) && !(exception instanceof OAuth2AuthenticationException)) {
            //在令牌断点捕捉到的非OAuth2的才需要转
            return convert(exception);
        }
        return exception;
    }

    /**
     * 执行转换
     *
     * @param exception 异常
     * @return 转换结果
     */
    public OAuth2AuthenticationException convert(AuthenticationException exception) {
        //抛到这里的异常只有几种
        //1. 直接抛出的认证异常
        //2. 捕捉到别的异常被NestedAuthenticationException包装的异常
        //3. 什么ResponseStatusCode之类的那种不行
        String errorCode = foundOAuth2ErrorCode(exception);
        //转换
        return new OAuth2AuthenticationException(
                //本类负责转的是个错误id
                new OAuth2Error(errorCode),
                exception
        );
    }

    private String foundOAuth2ErrorCode(Exception exception) {
        //转错误
        OAuth2ErrorCode annotation = AnnotationUtils.findAnnotation(exception.getClass(), OAuth2ErrorCode.class);
        if (annotation != null) {
            return annotation.value();
        } else {
            String codeFromRegistry = lookupErrorCodeRegistry(exception);
            if (StringUtils.hasText(codeFromRegistry)) {
                return codeFromRegistry;
            }
        }
        if (exception instanceof NestedAuthenticationException && exception.getCause() instanceof Exception) {
            return foundOAuth2ErrorCode((Exception) exception.getCause());
        }
        return OAuth2ErrorCodes.SERVER_ERROR;
    }
}
