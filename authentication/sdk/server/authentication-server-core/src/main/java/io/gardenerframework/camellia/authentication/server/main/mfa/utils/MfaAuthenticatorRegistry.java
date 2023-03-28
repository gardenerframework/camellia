package io.gardenerframework.camellia.authentication.server.main.mfa.utils;

import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeContext;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeRequest;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.MfaAuthenticator;
import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * 认证服务器加载的mfa认证器注册表
 *
 * @author zhanghan30
 * @date 2023/3/6 12:42
 */
public interface MfaAuthenticatorRegistry {
    /**
     * 获取已经注册的服务类型清单
     *
     * @return 清单
     */
    Collection<String> getAuthenticatorNames();

    /**
     * 是否包含当前服务
     *
     * @param name 类型
     * @return 是否包含
     */
    default boolean hasAuthenticator(@NonNull String name) {
        Collection<String> authenticatorNames = getAuthenticatorNames();
        return !CollectionUtils.isEmpty(authenticatorNames) && authenticatorNames.contains(name);
    }


    /**
     * 获取用户认证服务
     *
     * @param name 类型
     * @return 服务
     */
    @Nullable
    <R extends ChallengeRequest, C extends Challenge, X extends ChallengeContext> MfaAuthenticator<R, C, X> getAuthenticator(@NonNull String name);
}
