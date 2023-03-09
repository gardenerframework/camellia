package io.gardenerframework.camellia.authentication.infra.challenge.core;

import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.exception.ChallengeInCooldownException;
import io.gardenerframework.camellia.authentication.infra.challenge.core.exception.ChallengeResponseServiceException;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeContext;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeRequest;
import lombok.NonNull;
import org.springframework.lang.Nullable;

/**
 * @author zhanghan30
 * @date 2023/2/21 12:39
 */
public interface ChallengeResponseService<
        R extends ChallengeRequest,
        C extends Challenge,
        X extends ChallengeContext> {
    /**
     * 发送挑战
     *
     * @param client   正在请求的客户端
     * @param scenario 场景
     * @param request  挑战请求
     * @return 挑战结果
     * @throws ChallengeResponseServiceException 发送问题
     * @throws ChallengeInCooldownException      发送冷却未结束
     */
    C sendChallenge(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull R request
    ) throws ChallengeResponseServiceException, ChallengeInCooldownException;

    /**
     * 验证响应是否符合预期
     *
     * @param client      正在请求的客户端
     * @param scenario    场景
     * @param challengeId 挑战id
     * @param response    挑战响应
     * @return 是否通过校验
     * @throws ChallengeResponseServiceException 校验过程发生问题
     */
    boolean verifyResponse(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId,
            @NonNull String response
    ) throws ChallengeResponseServiceException;

    /**
     * 加载上下文
     *
     * @param client      正在请求的客户端
     * @param scenario    场景
     * @param challengeId 挑战id
     * @return 上下文信息
     * @throws ChallengeResponseServiceException 加载出现问题
     */
    @Nullable
    X getContext(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId
    ) throws ChallengeResponseServiceException;

    /**
     * 关闭挑战，即释放资源
     *
     * @param client      正在请求的客户端
     * @param scenario    场景
     * @param challengeId 挑战id
     * @throws ChallengeResponseServiceException 关闭过程中遇到了问题
     */
    void closeChallenge(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId
    ) throws ChallengeResponseServiceException;
}
