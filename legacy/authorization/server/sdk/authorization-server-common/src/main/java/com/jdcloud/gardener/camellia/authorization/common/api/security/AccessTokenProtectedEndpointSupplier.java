package com.jdcloud.gardener.camellia.authorization.common.api.security;

import com.jdcloud.gardener.camellia.authorization.common.api.security.schema.AccessTokenProtectedEndpointSetting;

/**
 * 有些之前写的代码或者其它组件的代码也需要access token的保护，但他们没有办法添加注解
 * <p>
 * 这个类就是给出这些代码的类都有什么
 *
 * @author ZhangHan
 * @date 2022/5/14 13:20
 */
@FunctionalInterface
public interface AccessTokenProtectedEndpointSupplier {
    /**
     * 给出接口类
     *
     * @return 接口以及相关的配置
     */
    AccessTokenProtectedEndpointSetting getAccessTokenProtectedEndpoint();
}
