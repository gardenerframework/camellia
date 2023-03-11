package io.gardenerframework.camellia.authentication.server.main.mfa.exception.client;

import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEnginePreserved;
import io.gardenerframework.camellia.authentication.server.main.exception.AuthenticationServerAuthenticationExceptions;
import io.gardenerframework.camellia.authentication.server.main.exception.OAuth2ErrorCodes;
import io.gardenerframework.camellia.authentication.server.main.exception.annotation.OAuth2ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author zhanghan30
 * @date 2023/2/27 19:52
 */
@OAuth2ErrorCode(OAuth2ErrorCodes.INVALID_REQUEST)
@ResponseStatus(HttpStatus.BAD_REQUEST)
@AuthenticationServerEnginePreserved
public class BadMfaAuthenticationRequestException extends AuthenticationServerAuthenticationExceptions.ClientSideException {
    public BadMfaAuthenticationRequestException(String challengeId) {
        super(challengeId);
    }
}
