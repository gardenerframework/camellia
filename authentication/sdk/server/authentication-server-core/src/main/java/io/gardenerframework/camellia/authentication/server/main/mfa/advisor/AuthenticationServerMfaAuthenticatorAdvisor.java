package io.gardenerframework.camellia.authentication.server.main.mfa.advisor;

import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2023/2/24 18:51
 */
@FunctionalInterface
public interface AuthenticationServerMfaAuthenticatorAdvisor {
    /**
     * 是否应当进行mfa的决策
     *
     * @param request            http请求
     * @param client             请求客户端
     * @param authenticationType 当前正在执行的认证类型
     * @param user               用户
     * @param context            认证过程中的上下文
     * @return 执行mfa的认证器名称，返回{@code null}表示认为不需要进行mfa验证
     * @throws Exception 发生异常
     */
    @Nullable
    String getAuthenticator(
            @NonNull HttpServletRequest request,
            @Nullable OAuth2RequestingClient client,
            @NonNull String authenticationType,
            @NonNull User user,
            @NonNull Map<String, Object> context
    ) throws Exception;
}
