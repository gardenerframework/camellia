package io.gardenerframework.camellia.authentication.server.main.mfa.utils;

import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.MfaAuthenticationServerClientChallengeRequest;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.Map;

public interface MfaAuthenticationServerClientChallengeRequestFactory {
    /**
     * 生产{@link  MfaAuthenticationServerClientChallengeRequest}的工厂，实际上就是看某个名字的用户数据以及额外参数怎么转
     *
     * @param authenticatorName 认证器名称
     * @param client            访问客户端
     * @param scenario          场景
     * @param principal         登录用户身份
     * @param user              读取出来的用户信息
     * @param context           认证过程中发生的上下文
     * @return mfa服务器客户端发请求时需要的请求参数
     */
    @Nullable
    MfaAuthenticationServerClientChallengeRequest create(
            String authenticatorName,
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull Principal principal,
            @NonNull User user,
            @NonNull Map<String, Object> context
    );
}
