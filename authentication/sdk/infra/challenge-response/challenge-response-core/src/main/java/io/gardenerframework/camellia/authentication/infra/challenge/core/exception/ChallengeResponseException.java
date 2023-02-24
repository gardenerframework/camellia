package io.gardenerframework.camellia.authentication.infra.challenge.core.exception;

/**
 * @author zhanghan30
 * @date 2023/2/21 13:34
 */
public class ChallengeResponseException extends Exception {
    public ChallengeResponseException() {
    }

    public ChallengeResponseException(String message) {
        super(message);
    }

    public ChallengeResponseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChallengeResponseException(Throwable cause) {
        super(cause);
    }

    public ChallengeResponseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
