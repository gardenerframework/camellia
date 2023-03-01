package com.jdcloud.gardener.camellia.uac.common.exception;

/**
 * @author zhanghan30
 * @date 2022/9/16 10:30 下午
 */
public abstract class UacExceptionBase extends RuntimeException {
    protected UacExceptionBase() {
    }

    protected UacExceptionBase(String message) {
        super(message);
    }

    protected UacExceptionBase(String message, Throwable cause) {
        super(message, cause);
    }

    protected UacExceptionBase(Throwable cause) {
        super(cause);
    }

    protected UacExceptionBase(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
