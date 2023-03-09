package io.gardenerframework.camellia.authentication.infra.challenge.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.time.Duration;

/**
 * 挑战应答服务异常
 *
 * @author zhanghan30
 * @date 2023/2/20 17:29
 */
@AllArgsConstructor
@Getter
public class ChallengeInCooldownException extends ChallengeResponseException {
    @NonNull
    private final Duration timeRemaining;
}
