package io.gardenerframework.camellia.authentication.infra.challenge.core;

import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeContext;
import io.gardenerframework.camellia.authentication.infra.client.schema.RequestingClient;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.time.Duration;

/**
 * @author zhanghan30
 * @date 2023/2/20 17:04
 */
public interface ChallengeContextStore<X extends ChallengeContext> {
    /**
     * 保存上下文
     *
     * @param client      正在请求的客户端
     * @param scenario    场景id
     * @param challengeId 挑战id
     * @param context     场下问
     * @param ttl         有效期
     * @throws Exception 异常
     */
    void saveContext(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId,
            @NonNull X context,
            @NonNull Duration ttl
    ) throws Exception;

    /**
     * 加载上下文
     *
     * @param client      正在请求的客户端
     * @param scenario    场景
     * @param challengeId 挑战id
     * @return 上下文
     * @throws Exception 发生问题
     */
    @Nullable
    X loadContext(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId
    ) throws Exception;


    /**
     * 删除上下文
     *
     * @param client      正在请求的客户端
     * @param scenario    场景
     * @param challengeId 挑战id
     * @throws Exception 发生问题
     */
    void removeContext(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId
    ) throws Exception;
}
