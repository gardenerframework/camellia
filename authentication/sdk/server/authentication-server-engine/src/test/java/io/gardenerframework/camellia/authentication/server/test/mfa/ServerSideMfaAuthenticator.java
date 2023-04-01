package io.gardenerframework.camellia.authentication.server.test.mfa;

import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.challenge.core.annotation.ChallengeAuthenticator;
import io.gardenerframework.camellia.authentication.infra.challenge.core.exception.ChallengeInCooldownException;
import io.gardenerframework.camellia.authentication.infra.challenge.core.exception.ChallengeResponseServiceException;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeContext;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeRequest;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.MfaAuthenticator;
import io.gardenerframework.camellia.authentication.server.main.UserAuthenticationService;
import io.gardenerframework.camellia.authentication.server.main.annotation.AuthenticationType;
import io.gardenerframework.camellia.authentication.server.main.mfa.advisor.AuthenticationServerMfaAuthenticatorAdvisor;
import io.gardenerframework.camellia.authentication.server.main.schema.UserAuthenticationRequestToken;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * @author zhanghan30
 * @date 2023/3/31 17:44
 */
@Component
@ChallengeAuthenticator("server-side")
@AuthenticationType("server-side")
public class ServerSideMfaAuthenticator implements MfaAuthenticator<
        ServerSideMfaAuthenticator.ServerSideChallengeRequest,
        ServerSideMfaAuthenticator.ServerSideChallenge,
        ServerSideMfaAuthenticator.ServerSideChallengeContext
        >, AuthenticationServerMfaAuthenticatorAdvisor, UserAuthenticationService, DiscoveryClient {

    @Setter
    public static int port;

    @Override
    public ServerSideChallengeRequest createChallengeRequest(
            Map<String, Object> userData,
            @Nullable RequestingClient client,
            Class<? extends Scenario> scenario,
            @Nullable Map<String, Object> additionalArguments
    ) {
        return new ServerSideChallengeRequest();
    }

    @Override
    public ServerSideChallenge sendChallenge(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull ServerSideChallengeRequest request) throws ChallengeResponseServiceException, ChallengeInCooldownException {
        return ServerSideChallenge.builder()
                .id(UUID.randomUUID().toString())
                .expiryTime(Date.from(Instant.now().plus(Duration.ofSeconds(50))))
                .build();
    }

    @Override
    public boolean verifyResponse(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull String challengeId, @NonNull String response) throws ChallengeResponseServiceException {
        return "666".equals(response);
    }

    @Nullable
    @Override
    public ServerSideChallengeContext getContext(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull String challengeId) throws ChallengeResponseServiceException {
        return null;
    }

    @Override
    public void closeChallenge(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull String challengeId) throws ChallengeResponseServiceException {

    }

    @Override
    public UserAuthenticationRequestToken convert(@NonNull HttpServletRequest request, @Nullable OAuth2RequestingClient client, @NonNull Map<String, Object> context) throws AuthenticationException {
        return new UserAuthenticationRequestToken(
                ServerSideTestPrincipal.builder()
                        .name(UUID.randomUUID().toString())
                        .build()
        );
    }


    @Override
    public void authenticate(@NonNull UserAuthenticationRequestToken authenticationRequest, @Nullable OAuth2RequestingClient client, @NonNull User user, @NonNull Map<String, Object> context) throws AuthenticationException {

    }

    @Nullable
    @Override
    public String getAuthenticator(@NonNull HttpServletRequest request, @Nullable OAuth2RequestingClient client, @NonNull String authenticationType, @NonNull User user, @NonNull Map<String, Object> context) throws Exception {
        return "server-side".equals(authenticationType) ? authenticationType : null;
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public List<ServiceInstance> getInstances(String serviceId) {
        return Collections.singletonList(
                new ServiceInstance() {
                    @Override
                    public String getServiceId() {
                        return "test";
                    }

                    @Override
                    public String getHost() {
                        return "localhost";
                    }

                    @Override
                    public int getPort() {
                        return port;
                    }

                    @Override
                    public boolean isSecure() {
                        return false;
                    }

                    @Override
                    public URI getUri() {
                        return null;
                    }

                    @Override
                    public Map<String, String> getMetadata() {
                        return null;
                    }
                }
        );
    }

    @Override
    public List<String> getServices() {
        return Collections.singletonList("test");
    }

    public static class ServerSideChallengeRequest implements ChallengeRequest {

    }

    public static class ServerSideChallengeContext implements ChallengeContext {

    }

    @SuperBuilder
    @NoArgsConstructor
    @Getter
    @Setter
    public static class ServerSideChallenge extends Challenge {
        @Builder.Default
        private String extField = UUID.randomUUID().toString();
    }

    @SuperBuilder
    @EqualsAndHashCode(callSuper = true)
    public static class ServerSideTestPrincipal extends Principal {

    }
}
