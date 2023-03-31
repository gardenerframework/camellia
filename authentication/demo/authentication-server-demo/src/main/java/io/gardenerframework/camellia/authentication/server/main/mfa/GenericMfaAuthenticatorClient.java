package io.gardenerframework.camellia.authentication.server.main.mfa;

import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.client.MfaAuthenticationClientPrototype;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "mfa", decode404 = true)
public interface GenericMfaAuthenticatorClient extends MfaAuthenticationClientPrototype<Challenge> {
}
