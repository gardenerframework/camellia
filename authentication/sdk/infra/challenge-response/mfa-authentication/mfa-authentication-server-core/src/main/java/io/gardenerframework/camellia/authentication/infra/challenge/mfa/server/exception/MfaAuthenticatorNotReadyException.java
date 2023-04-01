package io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.exception;

import io.gardenerframework.fragrans.api.standard.error.exception.ApiErrorDetailsSupplier;
import io.gardenerframework.fragrans.api.standard.error.exception.client.TooManyRequestsException;
import lombok.Getter;
import lombok.NonNull;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class MfaAuthenticatorNotReadyException extends TooManyRequestsException implements ApiErrorDetailsSupplier {
    @NonNull
    @Getter
    private final Duration timeRemaining;

    public MfaAuthenticatorNotReadyException(@NonNull Duration timeRemaining) {
        super(timeRemaining.toString());
        this.timeRemaining = timeRemaining;
    }

    @Override
    public Map<String, Object> getDetails() {
        Map<String, Object> details = new HashMap<>();
        details.put("timeRemaining", timeRemaining);
        return details;
    }
}
