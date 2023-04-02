package io.gardenerframework.camellia.authentication.server.main.mfa.challenge;

import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeResponseService;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeContext;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeRequest;

/**
 * 标记接口，表达这是个认证服务器内部使用的mfa认证器(挑战应答服务)
 */
public interface AuthenticationServerMfaAuthenticator<R extends ChallengeRequest, C extends Challenge, X extends ChallengeContext> extends ChallengeResponseService<R, C, X> {

}
