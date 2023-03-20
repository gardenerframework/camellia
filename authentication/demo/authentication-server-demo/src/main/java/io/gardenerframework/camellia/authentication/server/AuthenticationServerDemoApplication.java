package io.gardenerframework.camellia.authentication.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.TokenSettings;

import java.time.Duration;
import java.util.UUID;

@SpringBootApplication
public class AuthenticationServerDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthenticationServerDemoApplication.class, args);
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        TokenSettings tokenSettings = TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofDays(2))
                .refreshTokenTimeToLive(Duration.ofDays(2))
                .build();
        return new InMemoryRegisteredClientRepository(
                RegisteredClient.withId(UUID.randomUUID().toString())
                        .clientName("测试客户端")
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
                        .redirectUri("https://www.baidu.com")
                        .scope(OidcScopes.OPENID)
                        .scope(OidcScopes.PROFILE)
                        .tokenSettings(tokenSettings)
                        .build());
    }

}
