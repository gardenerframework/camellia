package com.jdcloud.gardener.camellia.authorization.authentication.main.exception.client;

import com.jdcloud.gardener.camellia.authorization.authentication.main.event.schema.AuthenticationFailedEvent;
import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.AuthorizationServerAuthenticationExceptions;
import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.OAuth2ErrorCodes;
import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.annotation.OAuth2ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 用于表达是认证请求有问题
 * <p>
 * 通常都是参数错误
 * <p>
 * 通常来说这种异常不会引发{@link AuthenticationFailedEvent}
 * <p>
 * 因为还没到认证，只是参数传输有问题
 *
 * @author ZhangHan
 * @date 2022/4/26 16:27
 * @see BadAuthenticationRequestParameterException
 */
@OAuth2ErrorCode(OAuth2ErrorCodes.INVALID_REQUEST)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public abstract class BadAuthenticationRequestException extends AuthorizationServerAuthenticationExceptions.ClientSideException {
    public BadAuthenticationRequestException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public BadAuthenticationRequestException(String msg) {
        super(msg);
    }
}
