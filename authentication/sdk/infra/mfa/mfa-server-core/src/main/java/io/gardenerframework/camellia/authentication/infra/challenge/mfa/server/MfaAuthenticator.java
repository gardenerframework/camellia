package io.gardenerframework.camellia.authentication.infra.challenge.mfa.server;

import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeResponseService;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeContext;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeRequest;

/**
 * 标记接口，用来表达这事给mfa服务器使用的挑战应答服务
 *
 * @author zhanghan30
 * @date 2023/3/29 13:54
 */
public interface MfaAuthenticator<
        R extends ChallengeRequest,
        C extends Challenge,
        X extends ChallengeContext> extends ChallengeResponseService<R, C, X> {
}
