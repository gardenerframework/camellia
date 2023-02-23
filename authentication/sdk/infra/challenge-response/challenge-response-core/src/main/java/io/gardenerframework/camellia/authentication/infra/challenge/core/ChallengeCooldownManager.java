package io.gardenerframework.camellia.authentication.infra.challenge.core;

import io.gardenerframework.camellia.authentication.infra.client.schema.RequestingClient;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.time.Duration;

/**
 * @author zhanghan30
 * @date 2023/2/20 17:20
 */
public interface ChallengeCooldownManager {
    /**
     * 获取冷却的剩余时间
     *
     * @param client   正在请求的客户端
     * @param scenario 场景
     * @param timerId  冷却计时器id
     * @return 剩余时间
     * @throws Exception 发生问题
     */
    @Nullable
    Duration getTimeRemaining(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String timerId
    ) throws Exception;

    /**
     * 开始冷却
     *
     * @param client   正在请求的客户端
     * @param scenario 场景
     * @param timerId  计时器id
     * @param ttl      冷却时间
     * @return 是否由当前调用开始冷却(多并发场景应当只有一个冷却发生)
     * @throws Exception 发生问题
     */
    boolean startCooldown(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String timerId,
            @NonNull Duration ttl
    ) throws Exception;
}

