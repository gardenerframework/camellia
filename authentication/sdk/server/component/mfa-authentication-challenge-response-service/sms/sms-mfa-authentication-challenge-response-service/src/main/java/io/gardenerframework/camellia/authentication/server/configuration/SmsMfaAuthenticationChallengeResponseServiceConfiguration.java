package io.gardenerframework.camellia.authentication.server.configuration;

import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.SmsMfaAuthenticationChallengeResponseService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(
        basePackageClasses = SmsMfaAuthenticationChallengeResponseService.class,
        includeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = SmsMfaAuthenticationChallengeResponseService.class
                )
        }
)
public class SmsMfaAuthenticationChallengeResponseServiceConfiguration {
}
