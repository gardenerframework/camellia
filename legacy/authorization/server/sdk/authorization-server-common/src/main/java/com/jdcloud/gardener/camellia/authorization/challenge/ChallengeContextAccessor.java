package com.jdcloud.gardener.camellia.authorization.challenge;

import com.jdcloud.gardener.camellia.authorization.challenge.schema.Challenge;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.ChallengeContext;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.ChallengeRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/**
 * 上下文以服务的类名作为命名空间，之后附加上challengeId进行隔离
 *
 * @author ZhangHan
 * @date 2022/5/24 19:10
 */
@FunctionalInterface
public interface ChallengeContextAccessor {
    /**
     * 指定挑战类，加载与这个挑战类有关的上下文
     *
     * @param clazz       挑战类
     * @param challengeId 挑战id
     * @return 挑战上下文，如果挑战上下文已失效则返回null
     */
    @Nullable
    ChallengeContext getContext(Class<? extends ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge>> clazz, String challengeId);

    /**
     * 基于服务类获得挑战上下文
     *
     * @param service     服务类
     * @param challengeId 挑战上下文
     * @return 挑战上下文，如果挑战上下文已失效则返回null
     */
    @SuppressWarnings("unchecked")
    @Nullable
    default ChallengeContext getContext(ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge> service, String challengeId) {
        return getContext((Class<ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge>>) ClassUtils.getUserClass(service), challengeId);
    }
}
