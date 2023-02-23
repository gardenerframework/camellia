package com.jdcloud.gardener.camellia.authorization.challenge.configuration;

import com.jdcloud.gardener.camellia.authorization.challenge.DefaultChallengeContextFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ZhangHan
 * @date 2022/5/31 17:39
 */
@Configuration
public class AuthorizationServerCommonChallengeConfiguration {
    @Bean
    public DefaultChallengeContextFactory defaultChallengeContextFactory() {
        return new DefaultChallengeContextFactory();
    }
}
