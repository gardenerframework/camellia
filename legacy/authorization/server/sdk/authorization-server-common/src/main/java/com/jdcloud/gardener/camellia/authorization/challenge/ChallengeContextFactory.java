package com.jdcloud.gardener.camellia.authorization.challenge;

import com.jdcloud.gardener.camellia.authorization.challenge.schema.Challenge;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.ChallengeContext;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.ChallengeRequest;

/**
 * @author ZhangHan
 * @date 2022/5/31 17:32
 */
@FunctionalInterface
public interface ChallengeContextFactory<R extends ChallengeRequest, C extends ChallengeContext> {
    /**
     * 由请求创建一个挑战上下文
     *
     * @param request   请求
     * @param challenge 生成的挑战
     * @return 上下文
     */
    C createContext(R request, Challenge challenge);
}
