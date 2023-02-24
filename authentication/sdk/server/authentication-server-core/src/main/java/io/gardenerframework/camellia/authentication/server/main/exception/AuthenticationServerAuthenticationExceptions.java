package io.gardenerframework.camellia.authentication.server.main.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 认证服务器问题的包装空间
 *
 * @author zhanghan30
 * @date 2022/8/26 4:19 下午
 */
public interface AuthenticationServerAuthenticationExceptions {
    /**
     * 基准错误
     */
    class AuthenticationServerAuthenticationException extends AuthenticationException {

        public AuthenticationServerAuthenticationException(String msg, Throwable cause) {
            super(msg, cause);
        }

        public AuthenticationServerAuthenticationException(String msg) {
            super(msg);
        }
    }

    /**
     * 客户端错误
     */
    class ClientSideException extends AuthenticationServerAuthenticationException {

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
    class ServerSideException extends AuthenticationServerAuthenticationException {

        public ServerSideException(String msg, Throwable cause) {
            super(msg, cause);
        }

        public ServerSideException(String msg) {
            super(msg);
        }
    }
}
