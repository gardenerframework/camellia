package com.jdcloud.gardener.camellia.authorization.authentication.main.exception;

/**
 * @author zhanghan30
 * @date 2022/8/26 5:16 下午
 */
public interface CasExceptions {
    /**
     * 最基本的cas错误，用于子类继承
     *
     * @author zhanghan30
     * @date 2022/8/23 4:10 下午
     */
    class CasException extends AuthorizationServerAuthenticationExceptions.AuthorizationServerAuthenticationException {
        public CasException(String msg, Throwable cause) {
            super(msg, cause);
        }

        public CasException(String msg) {
            super(msg);
        }
    }

    class ClientSideException extends CasException {
        public ClientSideException(String msg, Throwable cause) {
            super(msg, cause);
        }

        public ClientSideException(String msg) {
            super(msg);
        }
    }

    class ServerSideException extends CasException {

        public ServerSideException(String msg, Throwable cause) {
            super(msg, cause);
        }

        public ServerSideException(String msg) {
            super(msg);
        }
    }

}
