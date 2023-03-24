package io.gardenerframework.camellia.authentication.server.administration.authorization.endpoint;

import io.gardenerframework.camellia.authentication.server.administration.authorization.schema.request.RemoveUserAuthorizedAuthorizationRequest;

import javax.validation.Valid;

/**
 * 用户授权管理的接口框架
 *
 * @author zhanghan30
 * @date 2023/3/23 10:37
 */
public interface UserAuthorizedOAuth2AuthorizationAdministrationEndpointSkeleton {
    /**
     * 删除给定的授权
     *
     * @param request http请求
     * @throws Exception 发生异常
     */
    void removeAuthorization(
            @Valid RemoveUserAuthorizedAuthorizationRequest request
    ) throws Exception;
}
