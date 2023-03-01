package com.jdcloud.gardener.camellia.authorization.authentication.mfa;

import com.jdcloud.gardener.camellia.authorization.authentication.mfa.schema.MfaAuthenticationChallengeRequest;
import com.jdcloud.gardener.camellia.authorization.challenge.ChallengeResponseService;
import com.jdcloud.gardener.camellia.authorization.challenge.annotation.UsingContextFactory;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.Challenge;
import com.jdcloud.gardener.fragrans.log.annotation.LogTarget;
import org.springframework.lang.Nullable;

/**
 * @author zhanghan30
 * @date 2021/12/28 10:57 上午
 */
@LogTarget("多因子认证服务")
public interface MfaAuthenticationChallengeResponseService extends ChallengeResponseService<MfaAuthenticationChallengeRequest, Challenge> {
    @Nullable
    @Override
    @UsingContextFactory(MfaAuthenticationChallengeContextFactory.class)
    Challenge sendChallenge(MfaAuthenticationChallengeRequest request);
}
