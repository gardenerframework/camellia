package io.gardenerframework.camellia.authentication.server.test.mfa;

import io.gardenerframework.camellia.authentication.infra.challenge.core.annotation.ChallengeAuthenticator;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.client.MfaClient;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author zhanghan30
 * @date 2023/3/31 17:40
 */
@ChallengeAuthenticator("server-side")
@FeignClient(name = "test", path = "/api")
public interface ServerSideAuthenticatorClient extends MfaClient<ServerSideMfaAuthenticator.ServerSideChallenge> {

}
