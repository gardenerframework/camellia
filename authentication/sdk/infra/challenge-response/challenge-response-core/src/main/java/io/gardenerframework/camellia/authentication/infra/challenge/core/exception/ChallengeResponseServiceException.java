package io.gardenerframework.camellia.authentication.infra.challenge.core.exception;

/**
 * 挑战应答服务异常
 *
 * @author zhanghan30
 * @date 2023/2/20 17:29
 */
public class ChallengeResponseServiceException extends RuntimeException {
    public ChallengeResponseServiceException(Throwable cause) {
        super(cause);
    }
}
