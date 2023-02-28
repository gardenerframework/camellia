package com.jdcloud.gardener.camellia.authorization.test.mfa;

import com.jdcloud.gardener.camellia.authorization.authentication.main.event.listener.AuthenticationEventListenerSkeleton;
import com.jdcloud.gardener.camellia.authorization.authentication.main.event.schema.AuthenticationFailedEvent;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
import com.jdcloud.gardener.camellia.authorization.authentication.mfa.MfaAuthenticationChallengeResponseService;
import com.jdcloud.gardener.camellia.authorization.authentication.mfa.schema.MfaAuthenticationChallengeRequest;
import com.jdcloud.gardener.camellia.authorization.challenge.WellKnownChallengeType;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.Challenge;
import org.springframework.context.event.EventListener;
import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * @author zhanghan30
 * @date 2022/5/12 10:03 下午
 */
@AuthenticationServerEngineComponent
public class InMemoryMfaAuthenticationService implements MfaAuthenticationChallengeResponseService, AuthenticationEventListenerSkeleton {
    private final Set<BasicPrincipal> failedUsers = new HashSet<>(100);
    private final Map<String, Challenge> sentRequests = new HashMap<>(100);
    private final Map<String, BasicPrincipal> challengedUserPrincipal = new HashMap<>(100);
    private final Map<String, String> challengedUser = new HashMap<>(100);
    private final Map<String, String> userIncompleteRequest = new HashMap<>(100);

    @Nullable
    @Override
    public Challenge sendChallenge(MfaAuthenticationChallengeRequest request) {
        User user = request.getUser();
        Collection<BasicPrincipal> principals = user.getPrincipals();
        String userId = user.getId();
        String incompleteChallengeId = userIncompleteRequest.get(userId);
        if (StringUtils.hasText(incompleteChallengeId)) {
            return sentRequests.get(incompleteChallengeId);
        }
        for (BasicPrincipal principal : principals) {
            if (failedUsers.contains(principal)) {
                String id = UUID.randomUUID().toString();
                Challenge challenge = new Challenge(
                        id,
                        WellKnownChallengeType.GOOGLE_AUTHENTICATOR,
                        Date.from(Instant.now().plus(Duration.ofSeconds(1000))),
                        null
                );
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
    public boolean validateResponse(String id, String response) {
        return true;
    }

    @Override
    public void closeChallenge(String id) {
        sentRequests.remove(id);
        failedUsers.remove(challengedUserPrincipal.remove(id));
        userIncompleteRequest.remove(challengedUser.remove(id));
    }

    @Override
    @EventListener
    public void onAuthenticationFailed(AuthenticationFailedEvent event) throws AuthenticationException {
        failedUsers.add(event.getPrincipal());
    }
}
