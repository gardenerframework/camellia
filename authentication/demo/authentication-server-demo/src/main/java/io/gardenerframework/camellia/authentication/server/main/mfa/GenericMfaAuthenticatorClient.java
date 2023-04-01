package io.gardenerframework.camellia.authentication.server.main.mfa;

import io.gardenerframework.camellia.authentication.infra.challenge.core.annotation.ChallengeAuthenticator;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.client.MfaClient;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "demo-mfa-authentication-server", decode404 = true)
@ChallengeAuthenticator("sms")
public interface GenericMfaAuthenticatorClient extends MfaClient<Challenge> {
}
