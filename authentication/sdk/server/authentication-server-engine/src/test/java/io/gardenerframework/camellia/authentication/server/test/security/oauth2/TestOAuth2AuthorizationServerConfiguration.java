package io.gardenerframework.camellia.authentication.server.test.security.oauth2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;
import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2021/12/28 4:30 下午
 */
@Configuration
public class TestOAuth2AuthorizationServerConfiguration {
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
}
