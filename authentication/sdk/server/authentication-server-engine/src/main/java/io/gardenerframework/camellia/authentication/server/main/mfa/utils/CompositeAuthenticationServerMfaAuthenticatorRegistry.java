package io.gardenerframework.camellia.authentication.server.main.mfa.utils;

import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeContext;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeRequest;
import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEngineComponent;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.AuthenticationServerMfaAuthenticator;
import io.gardenerframework.fragrans.log.GenericBasicLogger;
import io.gardenerframework.fragrans.log.GenericLoggerStaticAccessor;
import io.gardenerframework.fragrans.log.common.schema.reason.NotFound;
import io.gardenerframework.fragrans.log.schema.content.GenericBasicLogContent;
import io.gardenerframework.fragrans.log.schema.details.Detail;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.util.Collection;

/**
 * @author zhanghan30
 * @date 2023/2/27 14:28
 */
@RequiredArgsConstructor
@Slf4j
@AuthenticationServerEngineComponent
public class CompositeAuthenticationServerMfaAuthenticatorRegistry {
    private final Collection<AuthenticationServerMfaAuthenticatorRegistry> registries;
    private GenericBasicLogger basicLogger;

    @Nullable
    public <R extends ChallengeRequest, C extends Challenge, X extends ChallengeContext> AuthenticationServerMfaAuthenticator<R, C, X> getAuthenticator(@NonNull String name) {
        for (AuthenticationServerMfaAuthenticatorRegistry registry : registries) {
            AuthenticationServerMfaAuthenticator<R, C, X> authenticator = registry.getAuthenticator(name);
            if (authenticator != null) {
                return authenticator;
            }
        }
        GenericLoggerStaticAccessor.basicLogger().warn(
                log,
                GenericBasicLogContent.builder()
                        .what(AuthenticationServerMfaAuthenticator.class)
                        .how(new NotFound())
                        .detail(new Detail() {
                            private final String authenticatorName = name;
                        })
                        .build(),
                null
        );
        return null;
    }
}
