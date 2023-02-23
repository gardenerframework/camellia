package com.jdcloud.gardener.camellia.uac.application.connector.rbac.exception;

/**
 * @author zhanghan30
 * @date 2022/11/17 20:57
 */
public interface ApplicationRbacConnectorExceptions {
    class ClientSideException extends ApplicationRbacConnectorExceptionBase {
        public ClientSideException() {
        }

        public ClientSideException(String message) {
            super(message);
        }

        public ClientSideException(String message, Throwable cause) {
            super(message, cause);
        }

        public ClientSideException(Throwable cause) {
            super(cause);
        }

        public ClientSideException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
    class ServerSideException extends ApplicationRbacConnectorExceptionBase {
        public ServerSideException() {
        }

        public ServerSideException(String message) {
            super(message);
        }

        public ServerSideException(String message, Throwable cause) {
            super(message, cause);
        }

        public ServerSideException(Throwable cause) {
            super(cause);
        }

        public ServerSideException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
