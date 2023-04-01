package io.gardenerframework.camellia.authentication.server.administration.authorization.endpoint;

import io.gardenerframework.camellia.authentication.server.administration.authorization.schema.request.RemoveUserAuthorizedAuthorizationRequest;
import io.gardenerframework.camellia.authentication.server.administration.authorization.service.UserAuthorizedOAuth2AuthorizationAdministrationService;
import io.gardenerframework.camellia.authentication.server.administration.configuration.UserAuthorizedOAuth2AuthorizationAdministrationComponent;
import io.gardenerframework.camellia.authentication.server.common.api.group.AdministrationServerRestController;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;

/**
 * @author zhanghan30
 * @date 2023/3/24 15:21
 */
@AdministrationServerRestController
@RequiredArgsConstructor
@UserAuthorizedOAuth2AuthorizationAdministrationComponent
//不是oauth2授权服务器
@ConditionalOnMissingClass("org.springframework.security.oauth2.server.authorization.web.OAuth2TokenEndpointFilter")
public class UserAuthorizedOAuth2AuthorizationAdministrationEndpoint implements
        UserAuthorizedOAuth2AuthorizationAdministrationEndpointSkeleton {
    private final UserAuthorizedOAuth2AuthorizationAdministrationService service;

    @Override
    public void removeAuthorization(RemoveUserAuthorizedAuthorizationRequest request) throws Exception {
        service.removeOAuth2Authorization(request);
    }
}
