package io.gardenerframework.camellia.authentication.server.administration.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeRequest;
import io.gardenerframework.camellia.authentication.server.client.schema.response.ClientAppearance;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.AuthenticationServerMfaAuthenticatorChallengeRequestFactory;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import io.gardenerframework.camellia.authentication.server.user.schema.response.UserAppearance;
import lombok.NonNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2023/3/27 12:15
 */
@SpringBootApplication
public class UserAuthorizedOAuth2AuthorizationAdministrationServiceTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserAuthorizedOAuth2AuthorizationAdministrationServiceTestApplication.class, args);
    }

    @Bean
    public Converter<User, UserAppearance> userAppearanceConverter(ObjectMapper mapper) {
        return new Converter<User, UserAppearance>() {
            @Nullable
            @Override
            public UserAppearance convert(User source) {
                return mapper.convertValue(source, UserAppearance.class);
            }
        };
    }

    @Bean
    public Converter<RegisteredClient, ClientAppearance> clientAppearanceConverter(ObjectMapper mapper) {
        return new Converter<RegisteredClient, ClientAppearance>() {
            @Nullable
            @Override
            public ClientAppearance convert(RegisteredClient source) {
                return mapper.convertValue(source, ClientAppearance.class);
            }
        };
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        TokenSettings tokenSettings = TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofDays(2))
                .refreshTokenTimeToLive(Duration.ofDays(2))
                .build();
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .clientId("test")
                .clientSecret("{noop}123")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(new AuthorizationGrantType("user_authentication"))
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .redirectUri("http://127.0.0.1:8081/authorized")
                .redirectUri("whatever://another/whatever")
                .redirectUri("rco-client:///")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .tokenSettings(tokenSettings)
                .build();
        RegisteredClient noClientCredentialsClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .clientId("no-client")
                .clientSecret("{noop}1234")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(new AuthorizationGrantType("user_authentication"))
                .redirectUri("http://127.0.0.1:8081/authorized")
                .redirectUri("whatever://another/whatever")
                .redirectUri("rco-client:///")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .tokenSettings(tokenSettings)
                .build();
        return new InMemoryRegisteredClientRepository(registeredClient, noClientCredentialsClient);
    }

    @Bean
    public AuthenticationServerMfaAuthenticatorChallengeRequestFactory<ChallengeRequest> authenticationServerMfaAuthenticatorChallengeRequestFactory() {
        return new AuthenticationServerMfaAuthenticatorChallengeRequestFactory<ChallengeRequest>() {
            @Nullable
            @Override
            public ChallengeRequest create(String authenticatorName, @Nullable RequestingClient client, @NonNull Class scenario, @NonNull Principal principal, @NonNull User user, @NonNull Map context) {
                return null;
            }
        };
    }
}
