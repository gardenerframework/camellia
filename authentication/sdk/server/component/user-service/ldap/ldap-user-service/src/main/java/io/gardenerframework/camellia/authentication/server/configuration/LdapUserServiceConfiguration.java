package io.gardenerframework.camellia.authentication.server.configuration;

import io.gardenerframework.camellia.authentication.server.LdapUserServicePackage;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * @author zhanghan30
 * @date 2023/3/15 17:32
 */
@Configuration
@ComponentScan(
        basePackageClasses = LdapUserServicePackage.class,
        includeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ANNOTATION,
                        value = LdapUserServiceComponent.class
                )
        }
)
public class LdapUserServiceConfiguration {
}
