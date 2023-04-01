package io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.utils;

import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeContext;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeRequest;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.MfaAuthenticator;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collection;

/**
 * @author zhanghan30
 * @date 2023/3/29 13:51
 */
public interface MfaAuthenticatorRegistry {
    /**
     * 获取验证器的所有名称
     *
     * @return 验证器名称
     */
    Collection<String> getAuthenticatorNames();

    /**
     * 获取指定的认证器
     *
     * @param name 认证器名称
     * @return 认证器实例
     */
    @Nullable
    <R extends ChallengeRequest, C extends Challenge, X extends ChallengeContext,
            T extends MfaAuthenticator<R, C, X>>
    T getAuthenticator(@NonNull String name);
}
