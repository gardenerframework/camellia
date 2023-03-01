package com.jdcloud.gardener.camellia.authorization.test;

import com.jdcloud.gardener.camellia.authorization.authentication.main.client.ClientGroupProvider;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.credentials.PasswordCredentials;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.UsernamePrincipal;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.UserService;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
import com.jdcloud.gardener.camellia.authorization.challenge.ChallengeContextAccessor;
import com.jdcloud.gardener.camellia.authorization.challenge.ChallengeResponseService;
import com.jdcloud.gardener.camellia.authorization.challenge.exception.client.InvalidChallengeException;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.Challenge;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.ChallengeContext;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.ChallengeRequest;
import com.jdcloud.gardener.camellia.authorization.username.UsernameResolver;
import com.jdcloud.gardener.camellia.authorization.username.recovery.PasswordRecoveryService;
import com.jdcloud.gardener.camellia.authorization.username.recovery.schema.challenge.PasswordRecoveryChallengeRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2022/4/11 3:20 下午
 */
@SpringBootApplication
public class UsernamePasswordPluginTestApplication extends WebSecurityConfigurerAdapter {
    public static void main(String[] args) {
        SpringApplication.run(UsernamePasswordPluginTestApplication.class, args);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().ignoringAntMatchers("/**");
        http.authorizeRequests().anyRequest().permitAll();
    }

    @Bean
    public ChallengeContextAccessor challengeContextAccessor() {
        return new ChallengeContextAccessor() {
            @Nullable
            @Override
            public ChallengeContext getContext(Class<? extends ChallengeResponseService<? extends ChallengeRequest, ? extends Challenge>> clazz, String challengeId) {
                return new ChallengeContext(null, new Date(), false);
            }
        };
    }

    @Bean
    public UsernameResolver usernameResolver() {
        return (username, principalType) -> new UsernamePrincipal(username);
    }

    @Bean
    public UserService userService() {
        return new UserService() {
            @Nullable
            @Override
            public User authenticate(BasicPrincipal principal, PasswordCredentials credentials, @Nullable Map<String, Object> context) throws AuthenticationException {
                return User.builder()
                        .id(UUID.randomUUID().toString())
                        .credentials(credentials)
                        .principals(Collections.singletonList(principal)).build();
            }

            @Nullable
            @Override
            public User load(BasicPrincipal principal, @Nullable Map<String, Object> context) throws AuthenticationException, UnsupportedOperationException {
                return User.builder()
                        .id(UUID.randomUUID().toString())
                        .credentials(null)
                        .principals(Collections.singletonList(principal)).build();
            }
        };
    }

    @Bean
    public PasswordRecoveryService passwordRecoveryService() {
        return new PasswordRecoveryService() {
            @Override
            public void resetPassword(String challengeId, String password) {

            }

            @Nullable
            @Override
            public Challenge sendChallenge(PasswordRecoveryChallengeRequest request) {
                return null;
            }

            @Override
            public boolean validateResponse(String id, String response) throws InvalidChallengeException {
                return false;
            }

            @Override
            public void closeChallenge(String id) {

            }
        };
    }

    @Bean
    public ClientGroupProvider clientGroupProvider() {
        return RegisteredClient::getClientId;

    }
}
