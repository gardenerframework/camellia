package io.gardenerframework.camellia.authentication.server.main.event.support;

import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.server.main.event.schema.AuthenticationEvent;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2023/2/27 19:59
 */
public interface AuthenticationEventBuilder {
    /**
     * 构建认证事件
     *
     * @param builder            构建器
     * @param request            请求
     * @param authenticationType 认证类型
     * @param principal          登录名
     * @param client             客户端
     * @param context            上下文
     * @param <B>                构建器类型
     * @return 事件构建器，可以继续构建事件
     */
    default <B extends AuthenticationEvent.AuthenticationEventBuilder<?, ?>> B buildAuthenticationEvent(
            B builder,
            @NonNull HttpServletRequest request,
            @NonNull String authenticationType,
            @NonNull Principal principal,
            @Nullable OAuth2RequestingClient client,
            @NonNull Map<String, Object> context
    ) {
        builder.request(request)
                .authenticationType(authenticationType)
                .principal(principal)
                .client(client)
                .context(context);
        return builder;
    }
}
