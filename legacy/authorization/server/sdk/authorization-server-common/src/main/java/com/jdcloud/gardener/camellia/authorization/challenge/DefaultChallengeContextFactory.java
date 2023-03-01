package com.jdcloud.gardener.camellia.authorization.challenge;

import com.jdcloud.gardener.camellia.authorization.challenge.schema.Challenge;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.ChallengeContext;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.ChallengeEnvironment;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.ChallengeRequest;
import org.springframework.util.Assert;

/**
 * @author ZhangHan
 * @date 2022/5/31 17:33
 */
public class DefaultChallengeContextFactory implements ChallengeContextFactory<ChallengeRequest, ChallengeContext> {
    @Override
    public ChallengeContext createContext(ChallengeRequest request, Challenge challenge) {
        Assert.notNull(request, "request must not be null");
        Assert.notNull(challenge, "request must not be null");
        return new ChallengeContext(
                new ChallengeEnvironment(request.getHeaders(), request.getClientGroup(), request.getClient(), request.getUser()),
                challenge.getExpiresAt(),
                false
        );
    }
}
