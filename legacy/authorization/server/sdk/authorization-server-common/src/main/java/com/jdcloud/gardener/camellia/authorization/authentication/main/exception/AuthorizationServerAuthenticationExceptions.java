package com.jdcloud.gardener.camellia.authorization.authentication.main.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 认证服务器问题的包装空间
 *
 * @author zhanghan30
 * @date 2022/8/26 4:19 下午
 */
public interface AuthorizationServerAuthenticationExceptions {
    /**
     * 基准错误
     */
    class AuthorizationServerAuthenticationException extends AuthenticationException {

        public AuthorizationServerAuthenticationException(String msg, Throwable cause) {
            super(msg, cause);
        }

        public AuthorizationServerAuthenticationException(String msg) {
            super(msg);
        }
    }

    /**
     * 客户端错误
     */
    class ClientSideException extends AuthorizationServerAuthenticationException {

        public ClientSideException(String msg, Throwable cause) {
            super(msg, cause);
        }

        public ClientSideException(String msg) {
            super(msg);
        }
    }

    /**
     * 服务端错误
     */
    class ServerSideException extends AuthorizationServerAuthenticationException {

        public ServerSideException(String msg, Throwable cause) {
            super(msg, cause);
        }

        public ServerSideException(String msg) {
            super(msg);
        }
    }
}
