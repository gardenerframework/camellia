package io.gardenerframework.camellia.authentication.server.main.mfa.exception.client;

import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEnginePreserved;
import io.gardenerframework.camellia.authentication.server.main.exception.AuthenticationServerAuthenticationExceptions;
import io.gardenerframework.camellia.authentication.server.main.exception.OAuth2ErrorCodes;
import io.gardenerframework.camellia.authentication.server.main.exception.annotation.OAuth2ErrorCode;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.Duration;

/**
 * @author zhanghan30
 * @date 2023/2/27 19:28
 */
@OAuth2ErrorCode(OAuth2ErrorCodes.INVALID_REQUEST)
@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
@AuthenticationServerEnginePreserved
public class MfaAuthenticatorNotReadyException extends AuthenticationServerAuthenticationExceptions.ClientSideException {
    @NonNull
    @Getter
    private final Duration timeRemaining;

    public MfaAuthenticatorNotReadyException(@NonNull Duration timeRemaining) {
        super(timeRemaining.toString());
        this.timeRemaining = timeRemaining;
    }
}
