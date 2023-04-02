package io.gardenerframework.camellia.authentication.server.main.mfa.utils;

import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeContext;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeRequest;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.AuthenticationServerMfaAuthenticator;
import lombok.NonNull;
import org.springframework.lang.Nullable;

/**
 * 认证服务器加载的mfa认证器注册表
 *
 * @author zhanghan30
 * @date 2023/3/6 12:42
 */
public interface AuthenticationServerMfaAuthenticatorRegistry {
    /**
     * 获取用户认证服务
     *
     * @param name 类型
     * @return 服务
     */
    @Nullable
    <R extends ChallengeRequest, C extends Challenge, X extends ChallengeContext> AuthenticationServerMfaAuthenticator<R, C, X> getAuthenticator(@NonNull String name);
}
