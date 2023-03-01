package com.jdcloud.gardener.camellia.uac.application.connector.rbac.exception;

/**
 * @author zhanghan30
 * @date 2022/11/17 20:56
 */
public abstract class ApplicationRbacConnectorExceptionBase extends RuntimeException {
    public ApplicationRbacConnectorExceptionBase() {
    }

    public ApplicationRbacConnectorExceptionBase(String message) {
        super(message);
    }

    public ApplicationRbacConnectorExceptionBase(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationRbacConnectorExceptionBase(Throwable cause) {
        super(cause);
    }

    public ApplicationRbacConnectorExceptionBase(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
