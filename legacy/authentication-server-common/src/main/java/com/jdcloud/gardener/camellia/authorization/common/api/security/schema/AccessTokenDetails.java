package com.jdcloud.gardener.camellia.authorization.common.api.security.schema;

import com.jdcloud.gardener.camellia.authorization.authentication.main.client.schema.Client;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
import com.jdcloud.gardener.camellia.authorization.common.api.security.AccessTokenProtectedEndpoint;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

/**
 * 配合 {@link AccessTokenProtectedEndpoint}使用的的类
 * <p>
 * 标记有{@link AccessTokenProtectedEndpoint}的api接口可以尝试在参数中要求这个注解
 *
 * @author ZhangHan
 * @date 2022/5/14 2:45
 */
@NoArgsConstructor
@Data
public class AccessTokenDetails {
    /**
     * 令牌对应的客户端
     * <p>
     * 如果当前要求不关注令牌，而且也没有令牌，那就是空的
     */
    @Nullable
    private Client client;
    /**
     * 令牌对应的客户端在系统中存储的数据
     * <p>
     * 如果当前要求不关注令牌，而且也没有令牌，那就是空的
     */
    @Nullable
    private RegisteredClient registeredClient;
    /**
     * 令牌对应的用户
     * <p>
     * 如果当前要求不关注令牌，而且也没有令牌，那就是空的
     */
    @Nullable
    private User user;
}
