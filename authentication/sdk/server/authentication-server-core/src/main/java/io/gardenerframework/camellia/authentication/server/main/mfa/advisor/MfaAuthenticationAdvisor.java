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
public interface MfaAuthenticationAdvisor {
    /**
     * 是否应当进行mfa的决策
     *
     * @param request http请求
     * @param client  请求客户端
     * @param user    用户
     * @param context 认证过程中的上下文
     * @return 是否应当进行mfa
     * @throws Exception 发生异常
     */
    boolean isMfaRequired(
            @NonNull HttpServletRequest request,
            @Nullable OAuth2RequestingClient client,
            @NonNull User user,
            @NonNull Map<String, Object> context
    ) throws Exception;
}
