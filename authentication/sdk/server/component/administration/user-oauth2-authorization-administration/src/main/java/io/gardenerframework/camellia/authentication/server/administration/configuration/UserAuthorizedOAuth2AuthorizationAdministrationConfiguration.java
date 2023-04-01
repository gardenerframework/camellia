package io.gardenerframework.camellia.authentication.server.administration.configuration;

import io.gardenerframework.camellia.authentication.server.administration.UserAuthorizedOAuth2AuthorizationAdministrationPackage;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * @author zhanghan30
 * @date 2023/3/24 17:32
 */
@Configuration
@ComponentScan(
        basePackageClasses = UserAuthorizedOAuth2AuthorizationAdministrationPackage.class,
        includeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ANNOTATION,
                        value = UserAuthorizedOAuth2AuthorizationAdministrationComponent.class
                )
        }
)
public class UserAuthorizedOAuth2AuthorizationAdministrationConfiguration {
}
