package io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.endpoint;

import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.request.CloseChallengeRequest;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.request.SendChallengeRequest;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.request.VerifyResponseRequest;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.request.constraints.MfaAuthenticatorSupported;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.response.ListAuthenticatorsResponse;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.response.ResponseVerificationResponse;

import javax.validation.Valid;

/**
 * @author zhanghan30
 * @date 2023/3/29 13:23
 */
public interface MfaAuthenticationEndpointSkeleton<C extends Challenge> {
    /**
     * 列出所有支持的验证器名称
     *
     * @return 获取验证器名称
     * @throws Exception 发生的问题
     */
    ListAuthenticatorsResponse listAuthenticators() throws Exception;

    /**
     * 发送挑战
     *
     * @param authenticator 认证器
     * @param request       发送请求
     * @return 挑战
     * @throws Exception 发生的问题
     */
    C sendChallenge(
            @Valid @MfaAuthenticatorSupported String authenticator,
            @Valid SendChallengeRequest request
    ) throws Exception;

    /**
     * 结果验证请求
     *
     * @param authenticator 认证器
     * @param request       结果验证请求
     * @return 是否验证成功
     * @throws Exception 发生的问题
     */
    ResponseVerificationResponse verifyResponse(
            @Valid @MfaAuthenticatorSupported String authenticator,
            @Valid VerifyResponseRequest request
    ) throws Exception;

    /**
     * 关闭挑战
     *
     * @param authenticator 认证器
     * @param request       请求参数
     * @throws Exception 发生的问题
     */
    void closeChallenge(
            @Valid @MfaAuthenticatorSupported String authenticator,
            @Valid CloseChallengeRequest request
    ) throws Exception;
}
