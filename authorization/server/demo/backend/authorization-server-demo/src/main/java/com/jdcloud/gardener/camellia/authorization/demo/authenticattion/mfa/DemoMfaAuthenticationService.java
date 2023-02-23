package com.jdcloud.gardener.camellia.authorization.demo.authenticattion.mfa;

import com.jdcloud.gardener.camellia.authorization.authentication.main.client.ClientGroupProvider;
import com.jdcloud.gardener.camellia.authorization.authentication.main.client.schema.Client;
import com.jdcloud.gardener.camellia.authorization.authentication.main.event.listener.AuthenticationEventListenerSkeleton;
import com.jdcloud.gardener.camellia.authorization.authentication.main.event.schema.AuthenticationFailedEvent;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
import com.jdcloud.gardener.camellia.authorization.authentication.mfa.MfaAuthenticationChallengeResponseService;
import com.jdcloud.gardener.camellia.authorization.authentication.mfa.schema.MfaAuthenticationChallengeRequest;
import com.jdcloud.gardener.camellia.authorization.challenge.WellKnownChallengeType;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.Challenge;
import com.jdcloud.gardener.camellia.sms.authentication.SmsAuthenticationCodeClient;
import com.jdcloud.gardener.camellia.sms.authentication.SmsAuthenticationCodeEncoder;
import com.jdcloud.gardener.camellia.sms.authentication.SmsAuthenticationCodeStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * @author ZhangHan
 * @date 2022/5/15 0:30
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DemoMfaAuthenticationService implements MfaAuthenticationChallengeResponseService, AuthenticationEventListenerSkeleton {
    private final Set<BasicPrincipal> failedUsers = new HashSet<>(100);
    private final long ttl = 300;

    private final SmsAuthenticationCodeEncoder smsAuthenticationCodeEncoder;
    private final SmsAuthenticationCodeClient smsAuthenticationCodeClient;
    private final SmsAuthenticationCodeStore smsAuthenticationCodeStore;
    private final ClientGroupProvider clientGroupProvider;

    private final String codeSuffix = DemoMfaAuthenticationService.class.getCanonicalName();
    private final String principalNameSuffix = DemoMfaAuthenticationService.class.getCanonicalName() + "." + "user";

    @Nullable
    @Override
    public Challenge sendChallenge(MfaAuthenticationChallengeRequest request) {
        User user = request.getUser();
        String clientGroup = request.getClientGroup();
        Collection<BasicPrincipal> principals = user.getPrincipals();
        for (BasicPrincipal principal : principals) {
            if (failedUsers.contains(principal)) {
                String code = String.format("%06d", new SecureRandom().nextInt(999999 + 1));
                String encoded = smsAuthenticationCodeEncoder.encode(clientGroup, code, DemoMfaAuthenticationService.class.getCanonicalName());
                smsAuthenticationCodeClient.sendCode(request.getHeaders(), clientGroup, principal.getName(), code, DemoMfaAuthenticationService.class.getCanonicalName());
                String challengeId = UUID.randomUUID().toString();
                //保存发送的编码
                smsAuthenticationCodeStore.saveCode(clientGroup, challengeId, encoded, codeSuffix, Duration.ofSeconds(ttl));
                //再存一下用户登录名
                smsAuthenticationCodeStore.saveCode(clientGroup, challengeId, principal.getName(), principalNameSuffix, Duration.ofSeconds(ttl));
                return new Challenge(
                        challengeId,
                        WellKnownChallengeType.SMS,
                        Date.from(Instant.now().plus(Duration.ofSeconds(ttl))),
                        null
                );
            }
        }
        return null;
    }


    @Override
    public boolean validateResponse(String id, String response) {
        Client client = getClient();
        String clientGroup = clientGroupProvider.getClientGroup(getRegisteredClient());
        String code = smsAuthenticationCodeStore.getCode(clientGroup, id, codeSuffix);
        if (!Objects.equals(smsAuthenticationCodeEncoder.encode(clientGroup, response, codeSuffix), code)) {
            return false;
        }
        return true;
    }

    @Override
    public void closeChallenge(String id) {
        Client client = getClient();
        String clientGroup = clientGroupProvider.getClientGroup(getRegisteredClient());
        String principalName = smsAuthenticationCodeStore.getCode(clientGroup, id, principalNameSuffix);
        if (principalName == null) {
            //超过了ttl应该是
            return;
        }
        BasicPrincipal principal = null;
        for (BasicPrincipal failedUser : failedUsers) {
            if (Objects.equals(failedUser.getName(), principalName)) {
                principal = failedUser;
            }
        }
        smsAuthenticationCodeStore.removeCode(clientGroup, id, codeSuffix);
        smsAuthenticationCodeStore.removeCode(clientGroup, id, principalNameSuffix);
        failedUsers.remove(principal);
    }

    @Override
    @EventListener
    public void onAuthenticationFailed(AuthenticationFailedEvent event) throws AuthenticationException {
        failedUsers.add(event.getPrincipal());
    }

    @Nullable
    private Client getClient() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2ClientAuthenticationToken) {
            return new Client(
                    ((OAuth2ClientAuthenticationToken) authentication).getRegisteredClient().getClientId(),
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public String getCooldownKey(MfaAuthenticationChallengeRequest request) {
        Collection<BasicPrincipal> principals = request.getUser().getPrincipals();
        for (BasicPrincipal principal : principals) {
            if (failedUsers.contains(principal)) {
                //发送挑战的才有冷却上下文
                return String.format("%s.%s", request.getUser().getId(), request.getClientGroup());
            }
        }
        //不发送挑战的没有上下文
        return null;
    }

    @Override
    public long getCooldown() {
        return 60;
    }

    @Nullable
    private RegisteredClient getRegisteredClient() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2ClientAuthenticationToken) {
            return ((OAuth2ClientAuthenticationToken) authentication).getRegisteredClient();
        }
        return null;
    }
}
