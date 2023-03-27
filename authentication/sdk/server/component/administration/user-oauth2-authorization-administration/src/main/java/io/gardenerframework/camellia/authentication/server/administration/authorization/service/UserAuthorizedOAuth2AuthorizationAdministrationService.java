package io.gardenerframework.camellia.authentication.server.administration.authorization.service;

import io.gardenerframework.camellia.authentication.server.administration.authorization.schema.request.RemoveUserAuthorizedAuthorizationRequest;

/**
 * @author zhanghan30
 * @date 2023/3/24 15:23
 */
public interface UserAuthorizedOAuth2AuthorizationAdministrationService {
    /**
     * 实际落地移除授权的操作
     *
     * @param request 移除请求
     * @throws Exception 发生问题
     */
    void removeOAuth2Authorization(
            RemoveUserAuthorizedAuthorizationRequest request
    ) throws Exception;
}
