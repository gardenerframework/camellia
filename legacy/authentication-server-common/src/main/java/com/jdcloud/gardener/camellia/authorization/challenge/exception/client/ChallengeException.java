package com.jdcloud.gardener.camellia.authorization.challenge.exception.client;

/**
 * @author ZhangHan
 * @date 2022/5/17 0:14
 */
public abstract class ChallengeException extends RuntimeException {
    public ChallengeException() {
    }

    public ChallengeException(String message) {
        super(message);
    }

    public ChallengeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChallengeException(Throwable cause) {
        super(cause);
    }

    public ChallengeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
