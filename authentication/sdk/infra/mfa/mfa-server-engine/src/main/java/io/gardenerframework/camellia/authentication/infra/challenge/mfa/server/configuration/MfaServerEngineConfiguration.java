package io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.configuration;

import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.MfaServerEnginePackage;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * @author zhanghan30
 * @date 2023/3/29 14:27
 */
@Configuration
@ComponentScan(basePackageClasses = MfaServerEnginePackage.class, includeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = MfaServerEngineComponent.class)
})
public class MfaServerEngineConfiguration {
}
