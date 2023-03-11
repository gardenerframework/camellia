package io.gardenerframework.camellia.authentication.server.main.configuration;

import io.gardenerframework.camellia.authentication.server.PasswordEncryptionServicePackage;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(
        basePackageClasses = PasswordEncryptionServicePackage.class,
        includeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION,
                value = PasswordEncryptionServiceComponent.class)}
)
public class PasswordEncryptionServiceConfiguration {
}
