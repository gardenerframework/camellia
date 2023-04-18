package io.gardenerframework.camellia.authentication.server.main.mfa.challenge;

import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeCooldownManager;
import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeResponseService;
import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.challenge.core.exception.ChallengeResponseServiceException;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeRequest;
import io.gardenerframework.camellia.authentication.infra.challenge.core.utils.ChallengeAuthenticatorUtils;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.AbstractChallengeResponseService;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.support.GenericCachedChallengeContextStore;
import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEngineComponent;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.AuthenticationServerMfaChallenge;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.AuthenticationServerMfaChallengeContext;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.AuthenticationServerMfaChallengeRequest;
import io.gardenerframework.camellia.authentication.server.main.mfa.utils.CompositeAuthenticationServerMfaAuthenticatorRegistry;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import io.gardenerframework.fragrans.log.GenericLoggerStaticAccessor;
import io.gardenerframework.fragrans.log.common.schema.reason.NotFound;
import io.gardenerframework.fragrans.log.schema.content.GenericBasicLogContent;
import io.gardenerframework.fragrans.log.schema.details.Detail;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

@AuthenticationServerEngineComponent
@Slf4j
public class AuthenticationServerMfaChallengeResponseService extends AbstractChallengeResponseService<
        AuthenticationServerMfaChallengeRequest,
        AuthenticationServerMfaChallenge,
        AuthenticationServerMfaChallengeContext
        > implements Scenario {
    private final CompositeAuthenticationServerMfaAuthenticatorChallengeRequestFactory compositeRequestFactory = new CompositeAuthenticationServerMfaAuthenticatorChallengeRequestFactory();
    @Setter(value = AccessLevel.PRIVATE, onMethod = @__({@Autowired}))
    private CompositeAuthenticationServerMfaAuthenticatorRegistry registry;
    @Setter(value = AccessLevel.PRIVATE, onMethod = @__({@Autowired}))
    private Collection<AuthenticationServerMfaAuthenticatorChallengeRequestFactory<? extends ChallengeRequest>> requestFactories;

    public AuthenticationServerMfaChallengeResponseService(@NonNull ChallengeCooldownManager challengeCooldownManager, @NonNull GenericCachedChallengeContextStore challengeContextStore) {
        super(challengeCooldownManager, challengeContextStore.migrateType());
    }

    @Override
    protected boolean hasCooldown(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull AuthenticationServerMfaChallengeRequest request) {
        //内部有cd就是有，没有就是没有
        return false;
    }

    @Override
    @Nullable
    protected String getCooldownTimerId(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull AuthenticationServerMfaChallengeRequest request) {
        //内部有cd就是有，没有就是没有
        return null;
    }

    @Override
    protected int getCooldownTime(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull AuthenticationServerMfaChallengeRequest request) {
        return 0;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected AuthenticationServerMfaChallenge sendChallengeInternally(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull AuthenticationServerMfaChallengeRequest request, @NonNull Map<String, Object> payload) throws Exception {
        AuthenticationServerMfaAuthenticator authenticator = Objects.requireNonNull(registry.getAuthenticator(request.getAuthenticatorName()));
        ChallengeRequest challengeRequest = Objects.requireNonNull(compositeRequestFactory.create(
                request.getAuthenticatorName(),
                client,
                scenario,
                request.getPrincipal(),
                request.getUser(),
                request.getContext()
        ));
        //authenticator发送挑战并注入advisor要求的名称
        Challenge challengeFromAuthenticator = ChallengeAuthenticatorUtils.injectChallengeAuthenticatorName(authenticator.sendChallenge(
                client,
                AuthenticationServerMfaScenario.class,
                challengeRequest
        ), request.getAuthenticatorName());
        return AuthenticationServerMfaChallenge.builder()
                .id(challengeFromAuthenticator.getId())
                .target(challengeFromAuthenticator)
                .cooldownCompletionTime(challengeFromAuthenticator.getCooldownCompletionTime())
                //这里注意，这是保持当前服务的挑战上下文失效时间和内层一致的关键
                .expiryTime(challengeFromAuthenticator.getExpiryTime())
                .build();
    }

    @Override
    protected AuthenticationServerMfaChallengeContext createContext(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull AuthenticationServerMfaChallengeRequest request, @NonNull AuthenticationServerMfaChallenge challenge, @NonNull Map<String, Object> payload) {
        return new AuthenticationServerMfaChallengeContext(request.getAuthenticatorName(), request.getPrincipal(), (OAuth2RequestingClient) client, request.getUser());
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected boolean verifyChallengeInternally(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull String challengeId, @NonNull AuthenticationServerMfaChallengeContext context, @NonNull String response) throws Exception {
        ChallengeResponseService authenticator = registry.getAuthenticator(context.getAuthenticatorName());
        return Objects.requireNonNull(authenticator).verifyResponse(client, AuthenticationServerMfaScenario.class, challengeId, response);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void closeChallenge(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull String challengeId) throws ChallengeResponseServiceException {
        //这里要使用原始的场景
        AuthenticationServerMfaChallengeContext context = getContext(client, scenario, challengeId);
        //上下文没有过期，意味着内置的验证器可以关闭挑战
        if (context != null) {
            ChallengeResponseService authenticator = registry.getAuthenticator(context.getAuthenticatorName());
            //内嵌验证器完成挑战
            Objects.requireNonNull(authenticator).closeChallenge(client, AuthenticationServerMfaScenario.class, challengeId);
        }
        //调用父类
        super.closeChallenge(client, scenario, challengeId);
    }

    private class CompositeAuthenticationServerMfaAuthenticatorChallengeRequestFactory implements AuthenticationServerMfaAuthenticatorChallengeRequestFactory<ChallengeRequest> {
        @Nullable
        @Override
        public ChallengeRequest create(String authenticatorName, @Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull Principal principal, @NonNull User user, @NonNull Map<String, Object> context) {
            for (AuthenticationServerMfaAuthenticatorChallengeRequestFactory<? extends ChallengeRequest> requestFactory : requestFactories) {
                ChallengeRequest challengeRequest = requestFactory.create(authenticatorName, client, scenario, principal, user, context);
                if (challengeRequest != null) {
                    //中断
                    return challengeRequest;
                }
            }
            String authenticatorNameHolder = authenticatorName;
            GenericLoggerStaticAccessor.basicLogger().warn(
                    log,
                    GenericBasicLogContent.builder()
                            .what(AuthenticationServerMfaAuthenticatorChallengeRequestFactory.class)
                            .how(new NotFound())
                            .detail(new Detail() {
                                private final String authenticatorName = authenticatorNameHolder;
                            })
                            .build(),
                    null
            );
            return null;
        }
    }
}
