package com.jdcloud.gardener.camellia.authorization.authentication.main.exception;

/**
 * @author zhanghan30
 * @date 2022/11/9 15:54
 */
public interface OAuth2AuthenticationEngineExceptions {
    /**
     * 最基本的cas错误，用于子类继承
     *
     * @author zhanghan30
     * @date 2022/8/23 4:10 下午
     */
    class OAuth2AuthenticationEngineException extends AuthorizationServerAuthenticationExceptions.AuthorizationServerAuthenticationException {
        public OAuth2AuthenticationEngineException(String msg, Throwable cause) {
            super(msg, cause);
        }

        public OAuth2AuthenticationEngineException(String msg) {
            super(msg);
        }
    }

    class ClientSideException extends OAuth2AuthenticationEngineException {
        public ClientSideException(String msg, Throwable cause) {
            super(msg, cause);
        }

        public ClientSideException(String msg) {
            super(msg);
        }
    }

    class ServerSideException extends OAuth2AuthenticationEngineException {

        public ServerSideException(String msg, Throwable cause) {
            super(msg, cause);
        }

        public ServerSideException(String msg) {
            super(msg);
        }
    }
}
