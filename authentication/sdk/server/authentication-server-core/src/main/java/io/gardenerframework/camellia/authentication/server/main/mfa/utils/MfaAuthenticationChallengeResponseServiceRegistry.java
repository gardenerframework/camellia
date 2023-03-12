package io.gardenerframework.camellia.authentication.server.main.mfa.utils;

import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.MfaAuthenticationChallengeResponseService;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.MfaAuthenticationChallengeContext;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.MfaAuthenticationChallengeRequest;
import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * mfa挑战应答服务注册表
 *
 * @author zhanghan30
 * @date 2023/3/6 12:42
 */
public interface MfaAuthenticationChallengeResponseServiceRegistry {
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
    default boolean hasMfaAuthenticationChallengeResponseService(@NonNull String name) {
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
    <R extends MfaAuthenticationChallengeRequest, X extends MfaAuthenticationChallengeContext>
    MfaAuthenticationChallengeResponseService<R, X> getMfaAuthenticationChallengeResponseService(@NonNull String name);
}
