package io.gardenerframework.camellia.authentication.infra.challenge.core;

import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.time.Duration;

/**
 * @author zhanghan30
 * @date 2023/2/20 17:20
 */
public interface ChallengeResponseCooldownManager {
    /**
     * 获取冷却的剩余时间
     *
     * @param applicationId 应用id
     * @param scenario      场景
     * @param timerId       冷却计时器id
     * @return 剩余时间
     * @throws Exception 发生问题
     */
    @Nullable
    Duration getTimeRemaining(
            @NonNull String applicationId,
            @NonNull Class<? extends Scenario> scenario,
            String timerId
    ) throws Exception;

    /**
     * 开始冷却
     *
     * @param applicationId 应用id
     * @param scenario      场景
     * @param timerId       计时器id
     * @param ttl           冷却时间
     * @return 是否由当前调用开始冷却(多并发场景应当只有一个冷却发生)
     * @throws Exception 发生问题
     */
    boolean startCooldown(
            @NonNull String applicationId,
            @NonNull Class<? extends Scenario> scenario,
            String timerId,
            Duration ttl
    ) throws Exception;
}

