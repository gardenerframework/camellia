package com.jdcloud.gardener.camellia.authorization.authentication.configuration;

import com.jdcloud.gardener.camellia.authorization.authentication.mfa.MfaAuthenticationChallengeContextFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ZhangHan
 * @date 2022/5/31 17:50
 */
@Configuration
public class AuthorizationServerCommonMfaConfiguration {
    @Bean
    public MfaAuthenticationChallengeContextFactory mfaAuthenticationChallengeContextFactory() {
        return new MfaAuthenticationChallengeContextFactory();
    }
}
