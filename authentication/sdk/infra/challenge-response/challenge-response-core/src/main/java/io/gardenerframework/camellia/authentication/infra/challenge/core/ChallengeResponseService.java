package io.gardenerframework.camellia.authentication.infra.challenge.core;

import io.gardenerframework.camellia.authentication.infra.challenge.core.exception.ChallengeInCooldownException;
import io.gardenerframework.camellia.authentication.infra.challenge.core.exception.ChallengeResponseServiceException;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeRequest;
import lombok.NonNull;

/**
 * @author zhanghan30
 * @date 2023/2/21 12:39
 */
public interface ChallengeResponseService {
    /**
     * 发送挑战
     *
     * @param applicationId 应用id
     * @param scenario      场景
     * @param request       挑战请求
     * @return 挑战结果
     * @throws ChallengeResponseServiceException 发送问题
     * @throws ChallengeInCooldownException      发送冷却未结束
     */
    Challenge sendChallenge(
            @NonNull String applicationId,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull ChallengeRequest request
    ) throws ChallengeResponseServiceException, ChallengeInCooldownException;

    /**
     * 验证响应是否符合预期
     *
     * @param applicationId 应用id
     * @param scenario      场景
     * @param challengeId   挑战id
     * @param response      挑战响应
     * @return 是否通过校验
     * @throws ChallengeResponseServiceException 校验过程发生问题
     */
    boolean verifyResponse(
            @NonNull String applicationId,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId,
            @NonNull String response
    ) throws ChallengeResponseServiceException;

    /**
     * 给定的挑战是否已经通过了验证
     *
     * @param applicationId 应用id
     * @param scenario      场景
     * @param challengeId   挑战id
     * @return 是否已经完成了验证
     * @throws ChallengeResponseServiceException 检验过程中发生了问题
     */
    boolean isChallengeVerified(
            @NonNull String applicationId,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId
    ) throws ChallengeResponseServiceException;

    /**
     * 关闭挑战，即释放资源
     *
     * @param applicationId 应用id
     * @param scenario      场景
     * @param challengeId   挑战id
     * @throws ChallengeResponseServiceException 关闭过程中遇到了问题
     */
    void closeChallenge(
            @NonNull String applicationId,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String challengeId
    ) throws ChallengeResponseServiceException;
}
