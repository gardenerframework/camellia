package io.gardenerframework.camellia.authentication.server.main.mfa.challenge;


import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeResponseService;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.MfaAuthenticationChallengeContext;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.MfaAuthenticationChallengeRequest;

/**
 * @author zhanghan30
 * @date 2021/12/28 10:57 上午
 */
public interface MfaAuthenticationChallengeResponseService extends ChallengeResponseService<
        MfaAuthenticationChallengeRequest,
        Challenge,
        MfaAuthenticationChallengeContext> {
}
