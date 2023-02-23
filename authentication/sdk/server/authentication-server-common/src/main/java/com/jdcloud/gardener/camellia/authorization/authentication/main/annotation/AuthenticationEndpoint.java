package com.jdcloud.gardener.camellia.authorization.authentication.main.annotation;

import com.jdcloud.gardener.camellia.authorization.authentication.main.UserAuthenticationService;

/**
 * 支持使用的入口
 * <p>
 * 如果没有注解那就默认认为都支持
 *
 * @author ZhangHan
 * @date 2022/5/11 10:32
 * @see UserAuthenticationService
 */
public @interface AuthenticationEndpoint {
    Endpoint[] value() default {Endpoint.WEB, Endpoint.OAUTH2};

    /**
     * 认证入口，转换器和认证器都需要说明自己负责的入口
     */
    enum Endpoint {
        /**
         * 网页入口
         */
        WEB,
        /**
         * oauth2 token endpoint
         */
        OAUTH2
    }
}
