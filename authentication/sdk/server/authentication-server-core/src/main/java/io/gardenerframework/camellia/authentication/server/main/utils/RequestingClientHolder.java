package io.gardenerframework.camellia.authentication.server.main.utils;

import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.server.main.event.listener.AuthenticationEventListenerSkeleton;
import io.gardenerframework.camellia.authentication.server.main.event.listener.annotation.CareForAuthenticationServerEnginePreservedPrincipal;
import io.gardenerframework.camellia.authentication.server.main.event.schema.ClientAuthenticatedEvent;
import lombok.NonNull;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * @author zhanghan30
 * @date 2023/2/27 18:24
 */
public class RequestingClientHolder implements AuthenticationEventListenerSkeleton {

    @Override
    @CareForAuthenticationServerEnginePreservedPrincipal
    @EventListener
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public void onClientAuthenticated(ClientAuthenticatedEvent event) throws AuthenticationException {
        OAuth2RequestingClient client = event.getClient();
        if (client != null) {
            saveClient(client);
        }
    }

    private RequestingClientHolder() {

    }

    /**
     * 保存客户端
     * <p>
     *
     * @param client 客户端
     */
    private static void saveClient(
            @NonNull RequestingClient client
    ) {
        RequestContextHolder.currentRequestAttributes()
                .setAttribute(
                        RequestingClientHolder.class.getCanonicalName(),
                        client, RequestAttributes.SCOPE_REQUEST);
    }

    /**
     * 加载客户端
     *
     * @return 客户端
     */
    @Nullable
    public static OAuth2RequestingClient getClient() {
        return (OAuth2RequestingClient) RequestContextHolder.currentRequestAttributes()
                .getAttribute(
                        RequestingClientHolder.class.getCanonicalName(),
                        RequestAttributes.SCOPE_REQUEST
                );
    }
}
