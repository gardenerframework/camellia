package io.gardenerframework.camellia.authentication.server.main.mfa.challenge;

import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeCooldownManager;
import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeResponseService;
import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.challenge.core.exception.ChallengeResponseServiceException;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.AbstractChallengeResponseService;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.support.GenericCachedChallengeContextStore;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.support.GenericCachedChallengeStore;
import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEngineComponent;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.AuthenticationServerMfaAuthenticationChallenge;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.AuthenticationServerMfaAuthenticationChallengeContext;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.AuthenticationServerMfaAuthenticationChallengeRequest;
import io.gardenerframework.camellia.authentication.server.main.mfa.utils.CompositeAuthenticationServerMfaAuthenticatorRegistry;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.Map;
import java.util.Objects;

@AuthenticationServerEngineComponent
public class AuthenticationServerMfaAuthenticationChallengeResponseService extends AbstractChallengeResponseService<AuthenticationServerMfaAuthenticationChallengeRequest, AuthenticationServerMfaAuthenticationChallenge, AuthenticationServerMfaAuthenticationChallengeContext> implements Scenario {
    private final CompositeAuthenticationServerMfaAuthenticatorRegistry registry;

    public AuthenticationServerMfaAuthenticationChallengeResponseService(@NonNull GenericCachedChallengeStore challengeStore, @NonNull ChallengeCooldownManager challengeCooldownManager, @NonNull GenericCachedChallengeContextStore challengeContextStore, CompositeAuthenticationServerMfaAuthenticatorRegistry registry) {
        super(challengeStore.migrateType(), challengeCooldownManager, challengeContextStore.migrateType());
        this.registry = registry;
    }

    @Override
    protected boolean replayChallenge(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull AuthenticationServerMfaAuthenticationChallengeRequest request) {
        //不重放 - 因为都是代理，实际的服务重放了就是重放了，没有就是没重放
        return false;
    }

    @Override
    @Nullable
    protected String getRequestSignature(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull AuthenticationServerMfaAuthenticationChallengeRequest request) {
        //不重放自然不需要这个
        return null;
    }

    @Override
    protected boolean hasCooldown(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull AuthenticationServerMfaAuthenticationChallengeRequest request) {
        //内部有cd就是有，没有就是没有
        return false;
    }

    @Override
    @Nullable
    protected String getCooldownTimerId(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull AuthenticationServerMfaAuthenticationChallengeRequest request) {
        //内部有cd就是有，没有就是没有
        return null;
    }

    @Override
    protected int getCooldownTime(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull AuthenticationServerMfaAuthenticationChallengeRequest request) {
        return 0;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected AuthenticationServerMfaAuthenticationChallenge sendChallengeInternally(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull AuthenticationServerMfaAuthenticationChallengeRequest request, @NonNull Map<String, Object> payload) throws Exception {
        AuthenticationServerMfaAuthenticator authenticator = registry.getAuthenticator(request.getAuthenticatorName());
        AuthenticationServerMfaAuthenticationChallenge challenge = new AuthenticationServerMfaAuthenticationChallenge();
        challenge.setTarget(Objects.requireNonNull(authenticator).sendChallenge(
                client,
                AuthenticationServerMfaAuthenticationScenario.class,
                authenticator.authenticationContextToChallengeRequest(client, AuthenticationServerMfaAuthenticationScenario.class, request.getPrincipal(), request.getUser(), request.getContext())
        ));
        challenge.setId(challenge.getTarget().getId());
        challenge.setCooldownCompletionTime(challenge.getTarget().getCooldownCompletionTime());
        challenge.setExpiryTime(challenge.getTarget().getExpiryTime());
        return challenge;
    }

    @Override
    protected AuthenticationServerMfaAuthenticationChallengeContext createContext(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull AuthenticationServerMfaAuthenticationChallengeRequest request, @NonNull AuthenticationServerMfaAuthenticationChallenge challenge, @NonNull Map<String, Object> payload) {
        return new AuthenticationServerMfaAuthenticationChallengeContext(request.getAuthenticatorName(), request.getPrincipal(), (OAuth2RequestingClient) client, request.getUser());
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected boolean verifyChallengeInternally(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull String challengeId, @NonNull AuthenticationServerMfaAuthenticationChallengeContext context, @NonNull String response) throws Exception {
        ChallengeResponseService authenticator = registry.getAuthenticator(context.getAuthenticatorName());
        return Objects.requireNonNull(authenticator).verifyResponse(client, AuthenticationServerMfaAuthenticationScenario.class, challengeId, response);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void closeChallenge(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull String challengeId) throws ChallengeResponseServiceException {
        //这里要使用原始的场景
        AuthenticationServerMfaAuthenticationChallengeContext context = getContext(client, scenario, challengeId);
        if (context != null) {
            ChallengeResponseService authenticator = registry.getAuthenticator(context.getAuthenticatorName());
            //内嵌验证器完成挑战
            Objects.requireNonNull(authenticator).closeChallenge(client, AuthenticationServerMfaAuthenticationScenario.class, challengeId);
        }
        //调用父类
        super.closeChallenge(client, scenario, challengeId);
    }
}
