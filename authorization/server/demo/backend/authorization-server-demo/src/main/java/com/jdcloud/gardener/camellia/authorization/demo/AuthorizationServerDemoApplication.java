package com.jdcloud.gardener.camellia.authorization.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
import com.jdcloud.gardener.camellia.authorization.client.schema.response.ClientAppearance;
import com.jdcloud.gardener.camellia.authorization.user.schema.response.UserAppearance;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.TokenSettings;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2021/12/20 2:38 下午
 */
@SpringBootApplication
@EnableScheduling
public class AuthorizationServerDemoApplication {
    private static RSAKey generateRsa() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    public static void main(String[] args) {
        SpringApplication.run(AuthorizationServerDemoApplication.class, args);
    }

    @Bean
    public TaskScheduler scheduler() {
        return new ConcurrentTaskScheduler();
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
                        .clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).build())
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
                        .scope("shouhuoren")
                        .tokenSettings(tokenSettings)
                        .build(),
                RegisteredClient.withId(UUID.randomUUID().toString())
                        .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                        .clientId("api")
                        .clientSecret("{noop}1234")
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
                        .scope("shouhuoren")
                        .tokenSettings(tokenSettings)
                        .build());
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
                return new ClientAppearance(source.getClientId(), source.getClientName(),
                        "测试用的客户端，主要是用于第三方对接和测试时使用",
                        "data:image/png;base64,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");
            }
        };
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }
}
