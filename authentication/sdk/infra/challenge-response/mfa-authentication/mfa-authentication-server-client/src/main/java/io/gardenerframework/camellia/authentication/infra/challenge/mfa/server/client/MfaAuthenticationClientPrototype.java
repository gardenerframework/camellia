package io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.client;

import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.endpoint.MfaAuthenticationEndpointSkeleton;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.request.CloseChallengeRequest;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.request.SendChallengeRequest;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.request.VerifyResponseRequest;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.response.ListAuthenticatorsResponse;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.response.ResponseVerificationResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author zhanghan30
 * @date 2023/3/30 13:19
 */
public interface MfaAuthenticationClientPrototype<C extends Challenge> extends MfaAuthenticationEndpointSkeleton<C> {
    @Override
    @GetMapping("/mfa")
    ListAuthenticatorsResponse listAuthenticators() throws Exception;

    @PostMapping("/mfa/{authenticator}:send")
    @Override
    C sendChallenge(@PathVariable("authenticator") @Valid String authenticator, @Valid @RequestBody SendChallengeRequest request) throws Exception;

    @PostMapping("/mfa/{authenticator}:verify")
    @Override
    ResponseVerificationResponse verifyResponse(@PathVariable("authenticator") @Valid String authenticator, @Valid @RequestBody VerifyResponseRequest request) throws Exception;

    @PostMapping("/mfa/{authenticator}:close")
    @Override
    void closeChallenge(@PathVariable("authenticator") @Valid String authenticator, @Valid @RequestBody CloseChallengeRequest request) throws Exception;
}
