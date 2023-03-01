package com.jdcloud.gardener.camellia.authorization.username.recovery.endpoint;

import com.jdcloud.gardener.camellia.authorization.username.recovery.schema.request.RecoverPasswordRequest;
import com.jdcloud.gardener.camellia.authorization.username.recovery.schema.request.ResetPasswordRequest;
import com.jdcloud.gardener.camellia.authorization.username.recovery.schema.response.PasswordRecoveryChallengeResponse;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author zhanghan30
 * @date 2022/9/1 7:09 下午
 */
public interface PasswordRecoveryEndpointSkeleton {
    /**
     * 发送密码找回请求
     *
     * @param request 请求
     * @return 请求结果
     */
    default PasswordRecoveryChallengeResponse startRecovery(HttpServletRequest httpServletRequest, @Valid RecoverPasswordRequest request) {
        return null;
    }

    /**
     * 重置密码
     * <p>
     * 在重置密码的时候除去验证挑战id是否有效外，另外还要验证当前客户端是否可以重置密码，比如和回答了挑战的客户端是不是一个
     *
     * @param request 重置请求
     */
    default void resetPassword(@Valid ResetPasswordRequest request) {

    }
}
