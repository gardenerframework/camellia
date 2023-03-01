package com.jdcloud.gardener.camellia.authorization.authentication.main.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.web.server.ResponseStatusException;

/**
 * 类似{@link ResponseStatusException}的{@link OAuth2AuthenticationException}版本
 * <p>
 * 其只能嵌入一个{@link AuthenticationException}，不允许用其他异常
 *
 * @author zhanghan30
 * @date 2022/4/20 8:26 下午
 */
public class ResponseStatusCodeOAuth2AuthenticationException extends OAuth2AuthenticationException {
    @Getter
    private final HttpStatus httpStatus;

    public ResponseStatusCodeOAuth2AuthenticationException(String errorCode, HttpStatus httpStatus) {
        super(errorCode);
        this.httpStatus = httpStatus;
    }

    public ResponseStatusCodeOAuth2AuthenticationException(OAuth2Error error, HttpStatus httpStatus) {
        super(error);
        this.httpStatus = httpStatus;
    }

    public ResponseStatusCodeOAuth2AuthenticationException(OAuth2Error error, AuthenticationException cause, HttpStatus httpStatus) {
        super(error, cause);
        this.httpStatus = httpStatus;
    }

    public ResponseStatusCodeOAuth2AuthenticationException(OAuth2Error error, String message, HttpStatus httpStatus) {
        super(error, message);
        this.httpStatus = httpStatus;
    }

    public ResponseStatusCodeOAuth2AuthenticationException(OAuth2Error error, String message, AuthenticationException cause, HttpStatus httpStatus) {
        super(error, message, cause);
        this.httpStatus = httpStatus;
    }

    @Override
    @Nullable
    public synchronized AuthenticationException getCause() {
        return (AuthenticationException) super.getCause();
    }
}
