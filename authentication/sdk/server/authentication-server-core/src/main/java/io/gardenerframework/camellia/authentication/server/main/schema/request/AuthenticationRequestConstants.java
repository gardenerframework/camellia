package io.gardenerframework.camellia.authentication.server.main.schema.request;

/**
 * @author zhanghan30
 * @date 2023/2/24 15:10
 */
public interface AuthenticationRequestConstants {
    /**
     * 被引擎支持的授权方式
     */
    class GrantTypes {
        /**
         * 要求的授权类型是用户认证
         */
        public static final String USER_AUTHENTICATION = "user_authentication";
    }
}
