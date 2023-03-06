package io.gardenerframework.camellia.authentication.server.main.exception.client;

import io.gardenerframework.camellia.authentication.server.main.exception.AuthenticationServerAuthenticationExceptions;
import io.gardenerframework.camellia.authentication.server.main.exception.OAuth2ErrorCodes;
import io.gardenerframework.camellia.authentication.server.main.exception.annotation.OAuth2ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author zhanghan30
 * @date 2023/3/6 12:12
 */
@OAuth2ErrorCode(OAuth2ErrorCodes.INVALID_REQUEST)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadOAuth2AuthorizationCodeException extends
        AuthenticationServerAuthenticationExceptions.ClientSideException {
    public BadOAuth2AuthorizationCodeException(String code) {
        super(code);
    }
}
