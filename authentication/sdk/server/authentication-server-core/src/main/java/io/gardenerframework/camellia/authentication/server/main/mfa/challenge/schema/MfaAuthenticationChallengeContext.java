package io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema;

import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.common.data.serialization.SerializationVersionNumber;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeContext;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

/**
 * @author zhanghan30
 * @date 2021/12/28 10:58 上午
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class MfaAuthenticationChallengeContext implements ChallengeContext,
        MfaAuthenticationChallengeContextAutoSavingContract{
    private static final long serialVersionUID = SerializationVersionNumber.version;
    /**
     * 触发当前mfa认证的登录名
     */
    @NonNull
    private Principal principal;
    /**
     * 当时正在请求的客户端
     */
    @Nullable
    private RequestingClient client;
    /**
     * 认证器
     */
    @NonNull
    private String authenticator;
    /**
     * 当时已经通过基本校验的用户
     */
    @NonNull
    private User user;
}
