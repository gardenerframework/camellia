package io.gardenerframework.camellia.authentication.infra.sms.engine.exceptions;

import lombok.Getter;
import lombok.NonNull;

import java.time.Duration;

/**
 * @author zhanghan30
 * @date 2023/2/14 20:00
 */
public class SmsAuthenticationInCooldownException extends RuntimeException {
    /**
     * 剩余时间
     */
    @Getter
    @NonNull
    private final Duration timeRemaining;

    public SmsAuthenticationInCooldownException(@NonNull Duration timeRemaining) {
        this.timeRemaining = timeRemaining;
    }
}
