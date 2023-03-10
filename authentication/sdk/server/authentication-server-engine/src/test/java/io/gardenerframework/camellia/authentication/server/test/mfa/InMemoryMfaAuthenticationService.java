package io.gardenerframework.camellia.authentication.server.test.mfa;

import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeCooldownManager;
import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.challenge.core.annotation.ChallengeAuthenticator;
import io.gardenerframework.camellia.authentication.infra.challenge.core.exception.ChallengeResponseServiceException;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.AbstractChallengeResponseService;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.support.GenericCachedChallengeContextStore;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.support.GenericCachedChallengeStore;
import io.gardenerframework.camellia.authentication.server.main.event.listener.AuthenticationEventListenerSkeleton;
import io.gardenerframework.camellia.authentication.server.main.event.schema.AuthenticationFailedEvent;
import io.gardenerframework.camellia.authentication.server.main.mfa.advisor.MfaAuthenticatorAdvisor;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.MfaAuthenticationChallengeResponseService;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.DefaultMfaAuthenticationChallengeContext;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.DefaultMfaAuthenticationChallengeRequest;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.MfaAuthenticationChallengeContext;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.MfaAuthenticationChallengeRequest;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.NonNull;
import org.springframework.context.event.EventListener;
import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * @author zhanghan30
 * @date 2022/5/12 10:03 ??????
 */
@Component
@ChallengeAuthenticator("test")
public class InMemoryMfaAuthenticationService extends
        AbstractChallengeResponseService<
                MfaAuthenticationChallengeRequest,
                Challenge,
                MfaAuthenticationChallengeContext>
        implements MfaAuthenticationChallengeResponseService<MfaAuthenticationChallengeRequest, MfaAuthenticationChallengeContext>, AuthenticationEventListenerSkeleton, MfaAuthenticatorAdvisor {
    private final Set<Principal> failedUsers = new HashSet<>(100);
    private final Map<String, Challenge> sentRequests = new HashMap<>(100);
    private final Map<String, Principal> challengedUserPrincipal = new HashMap<>(100);
    private final Map<String, String> challengedUser = new HashMap<>(100);
    private final Map<String, String> userIncompleteRequest = new HashMap<>(100);

    public InMemoryMfaAuthenticationService(
            @NonNull GenericCachedChallengeStore challengeStore,
            @NonNull ChallengeCooldownManager challengeCooldownManager,
            @NonNull GenericCachedChallengeContextStore challengeContextStore
    ) {
        super(challengeStore.migrateType(), challengeCooldownManager, challengeContextStore.migrateType());
    }

    @Override
    protected boolean replayChallenge(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull MfaAuthenticationChallengeRequest request) {
        //??????????????????????????????
        String incompleteChallengeId = userIncompleteRequest.get(request.getUser().getId());
        if (StringUtils.hasText(incompleteChallengeId)) {
            return sentRequests.get(incompleteChallengeId) != null;
        }
        //????????????????????????
        return false;
    }

    @Override
    protected @NonNull String getRequestSignature(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull MfaAuthenticationChallengeRequest request) {
        //?????????????????????????????????
        return request.getUser().getId();
    }

    @Override
    protected boolean hasCooldown(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull MfaAuthenticationChallengeRequest request) {
        return false;
    }

    @Override
    protected @NonNull String getCooldownTimerId(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull MfaAuthenticationChallengeRequest request) {
        return "";
    }

    @Override
    protected int getCooldownTime(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull MfaAuthenticationChallengeRequest request) {
        return 0;
    }

    @Override
    protected Challenge sendChallengeInternally(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull MfaAuthenticationChallengeRequest request, Map<String, Object> payload) throws Exception {
        User user = request.getUser();
        Collection<Principal> principals = user.getPrincipals();
        String userId = user.getId();
        String incompleteChallengeId = userIncompleteRequest.get(userId);
        if (StringUtils.hasText(incompleteChallengeId)) {
            return sentRequests.get(incompleteChallengeId);
        }
        for (Principal principal : principals) {
            if (failedUsers.contains(principal)) {
                String id = UUID.randomUUID().toString();
                Challenge challenge = Challenge.builder()
                        .id(id)
                        .expiryTime(Date.from(Instant.now().plus(Duration.ofSeconds(1000))))
                        .build();
                sentRequests.put(id, challenge);
                challengedUserPrincipal.put(id, principal);
                challengedUser.put(id, userId);
                userIncompleteRequest.put(userId, id);
                return challenge;
            }
        }
        return null;
    }

    @Override
    protected MfaAuthenticationChallengeContext createContext(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull MfaAuthenticationChallengeRequest request, @NonNull Challenge challenge, Map<String, Object> payload) {
        MfaAuthenticationChallengeContext mfaAuthenticationChallengeContext = new DefaultMfaAuthenticationChallengeContext();
        mfaAuthenticationChallengeContext.setUser(request.getUser());
        mfaAuthenticationChallengeContext.setPrincipal(request.getPrincipal());
        mfaAuthenticationChallengeContext.setClient((OAuth2RequestingClient) client);
        return mfaAuthenticationChallengeContext;
    }

    @Override
    protected boolean verifyChallengeInternally(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull String challengeId, @NonNull MfaAuthenticationChallengeContext context, @NonNull String response) throws Exception {
        return true;
    }

    @Override
    public void closeChallenge(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull String challengeId) throws ChallengeResponseServiceException {
        super.closeChallenge(client, scenario, challengeId);
        sentRequests.remove(challengeId);
        failedUsers.remove(challengedUserPrincipal.remove(challengeId));
        userIncompleteRequest.remove(challengedUser.remove(challengeId));
    }

    @Override
    @EventListener
    public void onAuthenticationFailed(AuthenticationFailedEvent event) throws AuthenticationException {
        failedUsers.add(event.getPrincipal());
    }

    @Nullable
    @Override
    public String getAuthenticator(@NonNull HttpServletRequest request, @Nullable OAuth2RequestingClient client, @NonNull String authenticationType, @NonNull User user, @NonNull Map<String, Object> context) throws Exception {
        for (Principal principal : user.getPrincipals()) {
            if (failedUsers.contains(principal)) {
                return "test";
            }
        }
        return null;
    }

    @Override
    public Challenge sendChallenge(@Nullable OAuth2RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull Principal principal, @NonNull User user, @NonNull Map<String, Object> context) throws Exception {
        MfaAuthenticationChallengeRequest mfaAuthenticationChallengeRequest = new DefaultMfaAuthenticationChallengeRequest();
        mfaAuthenticationChallengeRequest.setUser(user);
        mfaAuthenticationChallengeRequest.setPrincipal(principal);
        mfaAuthenticationChallengeRequest.setContext(context);
        return sendChallenge(client, scenario, mfaAuthenticationChallengeRequest);
    }
}
