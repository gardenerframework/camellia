package io.gardenerframework.camellia.authentication.server.main.schema;

import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.server.main.UserAuthenticationService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 登录请求的上下文
 *
 * @author zhanghan30
 * @date 2022/5/12 2:08 下午
 * @see LoginAuthenticationRequestToken
 */
@Getter
@Setter
@AllArgsConstructor
public class LoginAuthenticationRequestContext {
    /**
     * 由哪个服务来完成认证过程
     */
    @NonNull
    private final UserAuthenticationService userAuthenticationService;
    /**
     * 携带的http请求
     */
    @NonNull
    private final HttpServletRequest httpServletRequest;
    /**
     * 当前正在调用接口的客户端
     */
    @Nullable
    private final OAuth2RequestingClient client;
    /**
     * 认证上下文
     */
    @NonNull
    private final Map<String, Object> context;
}
