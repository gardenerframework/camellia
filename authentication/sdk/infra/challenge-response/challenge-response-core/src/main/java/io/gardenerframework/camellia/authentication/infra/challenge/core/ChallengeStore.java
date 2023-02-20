package io.gardenerframework.camellia.authentication.infra.challenge.core;

import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.common.Scenario;
import org.springframework.lang.Nullable;

import java.time.Duration;

/**
 * @author zhanghan30
 * @date 2023/2/20 18:07
 */
public interface ChallengeStore {
    /**
     * 保存挑战内容
     *
     * @param requestSignature 请求特征，也就是挑战的存储key
     * @param challenge        挑战
     * @param ttl              存储有效期
     * @throws Exception 保存异常
     */
    <C extends Challenge> void saveChallenge(
            String requestSignature,
            C challenge,
            Duration ttl
    ) throws Exception;

    /**
     * 读取挑战
     *
     * @param applicationId    应用id
     * @param requestSignature 请求特征，也就是挑战的存储key
     * @param scenario         场景
     * @return 挑战
     * @throws Exception 读取异常
     */
    @Nullable
    <C extends Challenge> C loadChallenge(
            String applicationId,
            String requestSignature,
            Class<? extends Scenario> scenario
    ) throws Exception;
}
