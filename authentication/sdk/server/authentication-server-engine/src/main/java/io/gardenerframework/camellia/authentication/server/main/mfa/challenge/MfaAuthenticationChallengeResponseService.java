package io.gardenerframework.camellia.authentication.server.main.mfa.challenge;

import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeCooldownManager;
import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeResponseService;
import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.challenge.core.exception.ChallengeResponseServiceException;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.AbstractChallengeResponseService;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.support.GenericCachedChallengeContextStore;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.support.GenericCachedChallengeStore;
import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEngineComponent;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.MfaAuthenticationChallengeContext;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.MfaAuthenticationChallengeRequest;
import io.gardenerframework.camellia.authentication.server.main.mfa.utils.MfaAuthenticatorRegistry;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.Map;
import java.util.Objects;

@AuthenticationServerEngineComponent
public class MfaAuthenticationChallengeResponseService extends AbstractChallengeResponseService<MfaAuthenticationChallengeRequest, Challenge, MfaAuthenticationChallengeContext> implements Scenario {
    private final MfaAuthenticatorRegistry registry;

    public MfaAuthenticationChallengeResponseService(@NonNull GenericCachedChallengeStore challengeStore, @NonNull ChallengeCooldownManager challengeCooldownManager, @NonNull GenericCachedChallengeContextStore challengeContextStore, MfaAuthenticatorRegistry registry) {
        super(challengeStore, challengeCooldownManager, challengeContextStore.migrateType());
        this.registry = registry;
    }

    @Override
    protected boolean replayChallenge(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull MfaAuthenticationChallengeRequest request) {
        //不重放 - 因为都是代理，实际的服务重放了就是重放了，没有就是没重放
        return false;
    }

    @Override
    @Nullable
    protected String getRequestSignature(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull MfaAuthenticationChallengeRequest request) {
        //不重放自然不需要这个
        return null;
    }

    @Override
    protected boolean hasCooldown(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull MfaAuthenticationChallengeRequest request) {
        //内部有cd就是有，没有就是没有
        return false;
    }

    @Override
    @Nullable
    protected String getCooldownTimerId(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull MfaAuthenticationChallengeRequest request) {
        //内部有cd就是有，没有就是没有
        return null;
    }

    @Override
    protected int getCooldownTime(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull MfaAuthenticationChallengeRequest request) {
        return 0;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected Challenge sendChallengeInternally(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull MfaAuthenticationChallengeRequest request, @NonNull Map<String, Object> payload) throws Exception {
        ChallengeResponseService authenticator = registry.getAuthenticator(request.getAuthenticatorName());
        return Objects.requireNonNull(authenticator).sendChallenge(
                client,
                MfaAuthenticationScenario.class,
                ((MfaAuthenticator) authenticator).authenticationContextToRequest(client, MfaAuthenticationScenario.class, request.getPrincipal(), request.getUser(), request.getContext())
        );
    }

    @Override
    protected MfaAuthenticationChallengeContext createContext(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull MfaAuthenticationChallengeRequest request, @NonNull Challenge challenge, @NonNull Map<String, Object> payload) {
        return new MfaAuthenticationChallengeContext(request.getAuthenticatorName(), request.getPrincipal(), (OAuth2RequestingClient) client, request.getUser());
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected boolean verifyChallengeInternally(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull String challengeId, @NonNull MfaAuthenticationChallengeContext context, @NonNull String response) throws Exception {
        ChallengeResponseService authenticator = registry.getAuthenticator(context.getAuthenticatorName());
        return Objects.requireNonNull(authenticator).verifyResponse(client, MfaAuthenticationScenario.class, challengeId, response);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void closeChallenge(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull String challengeId) throws ChallengeResponseServiceException {
        //这里要使用原始的场景
        MfaAuthenticationChallengeContext context = getContext(client, scenario, challengeId);
        if (context != null) {
            ChallengeResponseService authenticator = registry.getAuthenticator(context.getAuthenticatorName());
            //内嵌验证器完成挑战
            Objects.requireNonNull(authenticator).closeChallenge(client, MfaAuthenticationScenario.class, challengeId);
        }
        //调用父类
        super.closeChallenge(client, scenario, challengeId);
    }
}
