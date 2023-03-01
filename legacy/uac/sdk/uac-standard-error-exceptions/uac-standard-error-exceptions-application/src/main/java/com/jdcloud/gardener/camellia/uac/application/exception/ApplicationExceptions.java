package com.jdcloud.gardener.camellia.uac.application.exception;

import com.jdcloud.gardener.camellia.uac.common.exception.UacExceptionBase;

/**
 * @author ZhangHan
 * @date 2022/11/11 23:25
 */
public interface ApplicationExceptions {
    class ClientSideException extends UacExceptionBase {
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

    class ServerSideException extends UacExceptionBase {
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
