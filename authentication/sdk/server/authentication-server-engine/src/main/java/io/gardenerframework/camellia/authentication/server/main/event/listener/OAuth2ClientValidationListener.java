package io.gardenerframework.camellia.authentication.server.main.event.listener;

import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEngineComponent;
import io.gardenerframework.camellia.authentication.server.main.event.schema.ClientAuthenticatedEvent;
import io.gardenerframework.camellia.authentication.server.main.exception.client.ClientNotFoundException;
import io.gardenerframework.camellia.authentication.server.main.exception.client.UnauthorizedGrantTypeException;
import io.gardenerframework.camellia.authentication.server.main.exception.client.UnauthorizedScopeException;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.util.CollectionUtils;

import java.util.Objects;

/**
 * @author ZhangHan
 * @date 2022/4/28 1:21
 */
@AuthenticationServerEngineComponent
public class OAuth2ClientValidationListener implements AuthenticationEventListenerSkeleton {

    @Override
    @EventListener
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public void onClientAuthenticated(ClientAuthenticatedEvent event) throws AuthenticationException {
        //这个事件只有token接口触发
        OAuth2RequestingClient client = Objects.requireNonNull(event.getClient());
        RegisteredClient registeredClient = (RegisteredClient) event.getContext().get(RegisteredClient.class.getCanonicalName());
        if (registeredClient == null) {
            throw new ClientNotFoundException(client.getClientId());
        }
        if (!registeredClient.getAuthorizationGrantTypes().contains(new AuthorizationGrantType(Objects.requireNonNull(client).getGrantType()))) {
            throw new UnauthorizedGrantTypeException(client.getGrantType());
        }
        if (!CollectionUtils.isEmpty(client.getScopes())) {
            for (String requestedScope : client.getScopes()) {
                if (!registeredClient.getScopes().contains(requestedScope)) {
                    throw new UnauthorizedScopeException(requestedScope);
                }
            }
        }
    }
}
