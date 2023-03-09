package com.jdcloud.gardener.camellia.authorization.authentication.mfa;

import com.jdcloud.gardener.camellia.authorization.authentication.mfa.schema.MfaAuthenticationChallengeContext;
import com.jdcloud.gardener.camellia.authorization.authentication.mfa.schema.MfaAuthenticationChallengeRequest;
import com.jdcloud.gardener.camellia.authorization.challenge.ChallengeContextFactory;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.Challenge;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.ChallengeEnvironment;

/**
 * @author ZhangHan
 * @date 2022/5/31 17:42
 */
public class MfaAuthenticationChallengeContextFactory implements ChallengeContextFactory<MfaAuthenticationChallengeRequest, MfaAuthenticationChallengeContext> {
    @Override
    public MfaAuthenticationChallengeContext createContext(MfaAuthenticationChallengeRequest request, Challenge challenge) {
        return new MfaAuthenticationChallengeContext(
                new ChallengeEnvironment(request.getHeaders(), request.getClientGroup(), request.getClient(), request.getUser()),
                challenge.getExpiresAt(),
                false,
                request.getPrincipal()
        );
    }
}
