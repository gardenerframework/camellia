package io.gardenerframework.camellia.authentication.server.main.exception.client;


import io.gardenerframework.fragrans.api.standard.error.exception.ApiErrorDetailsSupplier;
import io.gardenerframework.fragrans.api.standard.error.exception.client.TooManyRequestsException;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SmsVerificationCodeNotReadyException extends TooManyRequestsException implements ApiErrorDetailsSupplier {
    private final Duration timeRemaining;

    public SmsVerificationCodeNotReadyException(String mobilePhoneNumber, Duration timeRemaining) {
        super(mobilePhoneNumber);
        this.timeRemaining = timeRemaining;
    }

    @Override
    public Map<String, Object> getDetails() {
        Map<String, Object> details = new HashMap<>();
        details.put("cooldownCompletionTime", Date.from(Instant.now().plus(timeRemaining)));
        return details;
    }
}
