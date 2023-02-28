package io.gardenerframework.camellia.authentication.server.main.oidc;

import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.NonNull;

import java.util.Map;
import java.util.Set;

/**
 * 用于定制化oidc的用户数据
 *
 * @author zhanghan30
 * @date 2022/4/20 12:59 下午
 */
@FunctionalInterface
public interface OidcUserInfoClaimsCustomizer {
    /**
     * 执行定制化
     *
     * @param client             当前访问的客户端
     * @param user               用户认证
     * @param oidcStandardClaims 已经初始化完的，基于标准的claims
     * @param authorizedScopes   授权客户端的scope
     */
    void customize(@NonNull OAuth2RequestingClient client, @NonNull User user, Map<String, Object> oidcStandardClaims, Set<String> authorizedScopes);
}
