package io.gardenerframework.camellia.authentication.infra.challenge.core;

import lombok.NonNull;

/**
 * @author zhanghan30
 * @date 2023/2/28 11:57
 */
@FunctionalInterface
public interface ChallengeAuthenticatorNameProvider {
    /**
     * 返回验证器的名称
     *
     * @return 名称
     */
    @NonNull
    String getChallengeAuthenticatorName();
}
