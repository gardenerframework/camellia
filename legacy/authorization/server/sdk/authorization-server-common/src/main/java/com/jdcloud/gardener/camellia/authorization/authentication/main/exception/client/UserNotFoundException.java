package com.jdcloud.gardener.camellia.authorization.authentication.main.exception.client;

import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.AuthorizationServerAuthenticationExceptions;
import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.OAuth2ErrorCodes;
import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.annotation.OAuth2ErrorCode;
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
