package io.gardenerframework.camellia.authentication.server.main.spring.support.oidc;

import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ZhangHan
 * @date 2022/5/21 11:22
 */
@FunctionalInterface
public interface OAuth2AuthorizationIdModifier {
    /**
     * 修改{@link OAuth2Authorization}所需的id
     *
     * @param originalId 原id
     * @param request    http请求
     * @param client     客户端(从网页认证流发起的没有客户端)
     * @param user       当前的登录用户，如果当前没有登录用户，那就是空的，比如客户端申请了client_credentials的token
     * @return id   如果认为不需要改变，就把 originalId 原样返回，否则返回新的id
     */
    String modify(String originalId, HttpServletRequest request, @Nullable OAuth2RequestingClient client, @Nullable User user);
}
