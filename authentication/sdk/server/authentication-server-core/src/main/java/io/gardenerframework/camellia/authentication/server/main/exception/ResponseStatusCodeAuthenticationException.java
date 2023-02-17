package io.gardenerframework.camellia.authentication.server.main.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.server.ResponseStatusException;

/**
 * 类似{@link ResponseStatusException}的{@link AuthenticationException}版本
 * <p>
 * 其只能嵌入一个{@link AuthenticationException}，不允许用其他异常
 *
 * @author ZhangHan
 * @date 2022/4/16 3:08
 */
public class ResponseStatusCodeAuthenticationException extends AuthorizationServerAuthenticationExceptions.AuthorizationServerAuthenticationException {
    @Getter
    private final HttpStatus status;

    public ResponseStatusCodeAuthenticationException(HttpStatus status, String msg) {
        super(msg);
        this.status = status;
    }

    public ResponseStatusCodeAuthenticationException(String msg, AuthenticationException cause, HttpStatus status) {
        super(msg, cause);
        this.status = status;
    }

    @Override
    @Nullable
    public synchronized AuthenticationException getCause() {
        return (AuthenticationException) super.getCause();
    }
}
