package io.gardenerframework.camellia.authentication.infra.challenge.core;

import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeContext;
import org.springframework.lang.Nullable;

import java.time.Duration;

/**
 * @author zhanghan30
 * @date 2023/2/20 17:04
 */
public interface ChallengeResponseContextStore<X extends ChallengeContext> {
    /**
     * 保存上下文
     * <p>
     * 当且仅当上下文没有被保存
     *
     * @param serviceType 服务类型
     * @param id          上下文id
     * @param context     上下文
     * @param ttl         有效期
     * @throws Exception 保存异常
     */
    void saveContext(
            Class<? extends AbstractChallengeResponseService> serviceType,
            String id,
            X context,
            Duration ttl
    ) throws Exception;

    /**
     * 更新上下文
     *
     * @param serviceType 服务类型
     * @param id          id
     * @param context     上下文
     * @param ttl         有效期
     * @throws Exception 更新异常
     */
    void updateContext(
            Class<? extends AbstractChallengeResponseService> serviceType,
            String id,
            X context,
            Duration ttl
    ) throws Exception;

    /**
     * 获取上下文
     *
     * @param id id
     * @return 上下文
     */
    @Nullable
    X getContext(
            String id
    );

    /**
     * 获取上下文的剩余时间
     *
     * @param id 上下文id
     * @return 剩余时间
     */
    @Nullable
    Duration getTimeRemaining(
            String id
    );

    /**
     * 删除上下文
     *
     * @param id 上下文id
     */
    void removeContext(
            String id
    );
}
