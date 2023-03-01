package com.jdcloud.gardener.camellia.authorization.authentication.main.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 专门为了非{@link AuthenticationException}用的包装类
 *
 * @author ZhangHan
 * @date 2022/5/16 1:55
 */
public class NestedAuthenticationException extends AuthorizationServerAuthenticationExceptions.AuthorizationServerAuthenticationException {
    public NestedAuthenticationException(Throwable cause) {
        super("", cause);
    }
}
