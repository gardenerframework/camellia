package com.jdcloud.gardener.camellia.authorization.test;

import com.jdcloud.gardener.camellia.authorization.authentication.main.PasswordEncryptionKeyStore;
import com.jdcloud.gardener.camellia.authorization.authentication.main.PasswordEncryptionService;
import com.jdcloud.gardener.camellia.authorization.challenge.ChallengeContextAccessor;
import com.jdcloud.gardener.camellia.authorization.challenge.ChallengeResponseService;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.Challenge;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.ChallengeContext;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.ChallengeRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.time.Duration;
import java.util.Date;

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
    public PasswordEncryptionService passwordEncryptionService() {
        return new PasswordEncryptionService() {
            @Override
            public String generateKey() throws Exception {
                return null;
            }

            @Override
            public String decrypt(String key, String password) throws Exception {
                return null;
            }
        };
    }

    @Bean
    public PasswordEncryptionKeyStore passwordEncryptionKeyStore() {
        return new PasswordEncryptionKeyStore() {
            @Override
            public void save(String id, String key, Duration ttl) {

            }

            @Nullable
            @Override
            public String load(String id) {
                return null;
            }

            @Nullable
            @Override
            public Long getTtl(String id) {
                return null;
            }
        };
    }
}
