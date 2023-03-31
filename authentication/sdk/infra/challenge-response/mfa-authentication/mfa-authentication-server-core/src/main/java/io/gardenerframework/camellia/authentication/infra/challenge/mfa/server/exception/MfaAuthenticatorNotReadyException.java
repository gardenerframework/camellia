package io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.exception;

import io.gardenerframework.fragrans.api.standard.error.exception.client.TooManyRequestsException;
import lombok.Getter;
import lombok.NonNull;

import java.time.Duration;

public class MfaAuthenticatorNotReadyException extends TooManyRequestsException {
    @NonNull
    @Getter
    private final Duration timeRemaining;

    public MfaAuthenticatorNotReadyException(@NonNull Duration timeRemaining) {
        super(timeRemaining.toString());
        this.timeRemaining = timeRemaining;
    }
}
