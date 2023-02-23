package com.jdcloud.gardener.camellia.authorization.username.recovery;

import com.jdcloud.gardener.camellia.authorization.challenge.ChallengeResponseService;
import com.jdcloud.gardener.camellia.authorization.challenge.annotation.ChallengeId;
import com.jdcloud.gardener.camellia.authorization.challenge.annotation.ValidateChallenge;
import com.jdcloud.gardener.camellia.authorization.challenge.annotation.ValidateChallengeEnvironment;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.Challenge;
import com.jdcloud.gardener.camellia.authorization.username.recovery.schema.challenge.PasswordRecoveryChallengeRequest;

/**
 * 密码找回服务类
 *
 * @author ZhangHan
 * @date 2022/5/13 20:48
 */
public interface PasswordRecoveryService extends ChallengeResponseService<PasswordRecoveryChallengeRequest, Challenge> {
    /**
     * 执行密码重置
     *
     * @param challengeId 应答成功的挑战id
     * @param password    新密码
     */
    @ValidateChallenge
    @ValidateChallengeEnvironment
    void resetPassword(@ChallengeId String challengeId, String password);
}
