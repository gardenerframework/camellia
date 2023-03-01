package com.jdcloud.gardener.camellia.authorization.authentication.main.client;

import com.jdcloud.gardener.camellia.authorization.authentication.main.client.schema.Client;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

/**
 * 用于给出当前安全环境下的应用组
 * <p>
 * 开发人员基于client id给出当前应用的应用应该是什么
 * <p>
 * 需要注意的是，一个应用有多个client id是正常现象
 *
 * @author zhanghan30
 * @date 2022/4/25 6:51 下午
 */
@FunctionalInterface
public interface ClientGroupProvider {
    /**
     * 给出应用组
     *
     * @param registeredClient oauth2 的注册客户端，
     *                         和{@link Client}不一样，这个客户端包含了客户端的所有信息，而不是客户端的请求信息，
     *                         另外不是oauth2的token endpoint那肯定就是没有
     * @return 应用组
     */
    String getClientGroup(@Nullable RegisteredClient registeredClient);
}
