package io.gardenerframework.camellia.authentication.server.main.event.listener;

import com.jdcloud.gardener.camellia.authorization.authentication.main.client.schema.Client;
import com.jdcloud.gardener.camellia.authorization.authentication.main.event.schema.AuthenticateClientEvent;
import io.gardenerframework.camellia.authentication.server.main.exception.client.UnauthorizedGrantTypeException;
import io.gardenerframework.camellia.authentication.server.main.exception.client.UnauthorizedScopeException;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Objects;

/**
 * @author ZhangHan
 * @date 2022/4/28 1:21
 */
@Component
@AllArgsConstructor
public class OAuth2ClientAuthenticationListener implements AuthenticationEventListenerSkeleton {

    @Override
    @EventListener
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public void onAuthenticateClient(AuthenticateClientEvent event) throws AuthenticationException {
        //这个事件只有token接口触发
        Client client = event.getClient();
        RegisteredClient registeredClient = event.getRegisteredClient();
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
