package io.gardenerframework.camellia.authentication.server.main.event.schema;

import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author ZhangHan
 * @date 2022/4/27 23:42
 */
@Getter
@SuperBuilder
public abstract class AuthenticationEvent {
    /**
     * http请求
     */
    @NonNull
    private final HttpServletRequest request;
    /**
     * 认证方式
     */
    @NonNull
    private final String authenticationType;
    /**
     * 登录请求的用户名以及类型
     */
    @NonNull
    private final Principal principal;
    /**
     * 但前准备要访问系统的客户端
     * <p>
     * 不是token endpoint 没有客户端
     */
    @Nullable
    private final OAuth2RequestingClient client;
    /**
     * 贯穿登录认证过程的上下文
     * <p>
     * 可以用来存取一些属性
     */
    @NonNull
    private final Map<String, Object> context;
}
