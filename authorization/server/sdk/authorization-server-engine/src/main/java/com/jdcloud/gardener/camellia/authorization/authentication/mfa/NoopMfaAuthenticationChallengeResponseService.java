package com.jdcloud.gardener.camellia.authorization.authentication.mfa;

import com.jdcloud.gardener.camellia.authorization.authentication.mfa.schema.MfaAuthenticationChallengeRequest;
import com.jdcloud.gardener.camellia.authorization.challenge.exception.client.InvalidChallengeException;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.Challenge;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * @author zhanghan30
 * @date 2022/8/23 9:51 上午
 */
@Component
@ConditionalOnMissingBean(value = MfaAuthenticationChallengeResponseService.class, ignored = NoopMfaAuthenticationChallengeResponseService.class)
public class NoopMfaAuthenticationChallengeResponseService implements MfaAuthenticationChallengeResponseService {
    /**
     * 不发送任何挑战
     *
     * @param request 挑战请求
     * @return null
     */
    @Override
    public Challenge sendChallenge(MfaAuthenticationChallengeRequest request) {
        return null;
    }

    /**
     * 自然也不需要回应任何挑战
     *
     * @param id       挑战id
     * @param response 响应
     * @return true
     * @throws InvalidChallengeException 非法的挑战场景
     */
    @Override
    public boolean validateResponse(String id, String response) throws InvalidChallengeException {
        return true;
    }

    /**
     * 自然也不需要关闭什么挑战
     *
     * @param id 挑战id
     */
    @Override
    public void closeChallenge(String id) {
        //do nothing
    }
}
