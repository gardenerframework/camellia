package io.gardenerframework.camellia.authentication.server.main.exception;

import io.gardenerframework.camellia.authentication.server.main.exception.annotation.OAuth2ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@OAuth2ErrorCode(OAuth2ErrorCodes.INVALID_REQUEST)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidKeyException extends AuthenticationServerAuthenticationExceptions.ClientSideException {
    public InvalidKeyException(String id) {
        super(id);
    }
}
