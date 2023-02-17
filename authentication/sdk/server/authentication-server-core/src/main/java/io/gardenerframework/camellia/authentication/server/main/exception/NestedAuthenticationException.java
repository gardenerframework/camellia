package io.gardenerframework.camellia.authentication.server.main.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 专门为了非{@link AuthenticationException}用的包装类
 *
 * @author ZhangHan
 * @date 2022/5/16 1:55
 */
public class NestedAuthenticationException extends AuthenticationServerAuthenticationExceptions.AuthorizationServerAuthenticationException {
    public NestedAuthenticationException(Throwable cause) {
        super("", cause);
    }
}
