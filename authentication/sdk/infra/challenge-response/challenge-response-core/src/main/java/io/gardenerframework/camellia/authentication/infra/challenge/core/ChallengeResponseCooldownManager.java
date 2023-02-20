package io.gardenerframework.camellia.authentication.infra.challenge.core;

import java.time.Duration;

/**
 * @author zhanghan30
 * @date 2023/2/20 17:20
 */
public interface ChallengeResponseCooldownManager {
    /**
     * 开始冷却
     * <p>
     * 当且仅当key还没有进入冷却
     *
     * @param key 冷却key
     * @param ttl 冷却时间
     * @return 是否开始
     * @throws Exception 遇到问题
     */
    boolean startCooldown(String key, Duration ttl) throws Exception;

    /**
     * 获取cd剩余时间
     *
     * @param key key
     * @return 剩余时间
     * @throws Exception 发生问题
     */
    Duration getTimeRemaining(String key) throws Exception;
}
