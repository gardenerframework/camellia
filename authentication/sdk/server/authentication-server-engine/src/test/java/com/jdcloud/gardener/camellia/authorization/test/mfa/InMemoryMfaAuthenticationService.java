//package com.jdcloud.gardener.camellia.authorization.test.mfa;
//
//import com.jdcloud.gardener.camellia.authorization.challenge.WellKnownChallengeType;
//import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
//import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
//import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
//import io.gardenerframework.camellia.authentication.infra.challenge.engine.AbstractChallengeResponseService;
//import io.gardenerframework.camellia.authentication.server.main.event.listener.AuthenticationEventListenerSkeleton;
//import io.gardenerframework.camellia.authentication.server.main.event.schema.AuthenticationFailedEvent;
//import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.MfaAuthenticationChallengeResponseService;
//import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.MfaAuthenticationChallengeContext;
//import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.MfaAuthenticationChallengeRequest;
//import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
//import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
//import org.springframework.context.event.EventListener;
//import org.springframework.lang.Nullable;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//
//import java.time.Duration;
//import java.time.Instant;
//import java.util.*;
//
///**
// * @author zhanghan30
// * @date 2022/5/12 10:03 下午
// */
//@Component
//public class InMemoryMfaAuthenticationService extends
//        AbstractChallengeResponseService<
//                MfaAuthenticationChallengeRequest,
//                Challenge,
//                MfaAuthenticationChallengeContext>
//        implements MfaAuthenticationChallengeResponseService, AuthenticationEventListenerSkeleton {
//    private final Set<Principal> failedUsers = new HashSet<>(100);
//    private final Map<String, Challenge> sentRequests = new HashMap<>(100);
//    private final Map<String, Principal> challengedUserPrincipal = new HashMap<>(100);
//    private final Map<String, String> challengedUser = new HashMap<>(100);
//    private final Map<String, String> userIncompleteRequest = new HashMap<>(100);
//
//    @Nullable
//    @Override
//    public Challenge sendChallenge(
//            RequestingClient client,
//            Class<? extends Scenario> sc,
//            MfaAuthenticationChallengeRequest request) {
//        User user = request.getUser();
//        Collection<Principal> principals = user.getPrincipals();
//        String userId = user.getId();
//        String incompleteChallengeId = userIncompleteRequest.get(userId);
//        if (StringUtils.hasText(incompleteChallengeId)) {
//            return sentRequests.get(incompleteChallengeId);
//        }
//        for (Principal principal : principals) {
//            if (failedUsers.contains(principal)) {
//                String id = UUID.randomUUID().toString();
//                Challenge challenge = new Challenge(
//                        id,
//                        WellKnownChallengeType.GOOGLE_AUTHENTICATOR,
//                        Date.from(Instant.now().plus(Duration.ofSeconds(1000))),
//                        null
//                );
//                sentRequests.put(id, challenge);
//                challengedUserPrincipal.put(id, principal);
//                challengedUser.put(id, userId);
//                userIncompleteRequest.put(userId, id);
//                return challenge;
//            }
//        }
//        return null;
//    }
//
//    @Override
//    public boolean validateResponse(String id, String response) {
//        return true;
//    }
//
//    @Override
//    public void closeChallenge(String id) {
//        sentRequests.remove(id);
//        failedUsers.remove(challengedUserPrincipal.remove(id));
//        userIncompleteRequest.remove(challengedUser.remove(id));
//    }
//
//    @Override
//    @EventListener
//    public void onAuthenticationFailed(AuthenticationFailedEvent event) throws AuthenticationException {
//        failedUsers.add(event.getPrincipal());
//    }
//}
