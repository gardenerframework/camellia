package io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema;

import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.common.data.serialization.SerializationVersionNumber;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.lang.Nullable;

/**
 * @author zhanghan30
 * @date 2021/12/28 10:58 上午
 */
@Getter
@Setter
@NoArgsConstructor
public class DefaultMfaAuthenticationChallengeContext implements MfaAuthenticationChallengeContext {
    private static final long serialVersionUID = SerializationVersionNumber.version;
    /**
     * 触发当前mfa认证的登录名
     * <p>
     * 用于重放认证成功事件
     */
    @NonNull
    private Principal principal;
    /**
     * 当时正在请求的客户端
     */
    @Nullable
    private OAuth2RequestingClient client;
    /**
     * 当时已经通过基本校验的用户
     */
    @NonNull
    private User user;
}
