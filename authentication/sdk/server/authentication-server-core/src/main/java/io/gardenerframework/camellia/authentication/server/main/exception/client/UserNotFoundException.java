package io.gardenerframework.camellia.authentication.server.main.exception.client;

import io.gardenerframework.camellia.authentication.server.main.exception.AuthorizationServerAuthenticationExceptions;
import io.gardenerframework.camellia.authentication.server.main.exception.OAuth2ErrorCodes;
import io.gardenerframework.camellia.authentication.server.main.exception.annotation.OAuth2ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 指定的用户名所对应的用户不存在
 *
 * @author ZhangHan
 * @date 2022/5/19 23:08
 */
@OAuth2ErrorCode(OAuth2ErrorCodes.UNAUTHORIZED)
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserNotFoundException extends AuthorizationServerAuthenticationExceptions.ClientSideException {
    public UserNotFoundException(String username) {
        super(username);
    }
}
