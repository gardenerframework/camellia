package io.gardenerframework.camellia.authentication.server.main.exception.client;

import io.gardenerframework.camellia.authentication.server.main.exception.AuthenticationServerAuthenticationExceptions;
import io.gardenerframework.camellia.authentication.server.main.exception.OAuth2ErrorCodes;
import io.gardenerframework.camellia.authentication.server.main.exception.annotation.OAuth2ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@OAuth2ErrorCode(OAuth2ErrorCodes.UNAUTHORIZED)
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class BadSmsVerificationCodeException extends AuthenticationServerAuthenticationExceptions.ClientSideException {
    public BadSmsVerificationCodeException(String msg) {
        super(msg);
    }
}
