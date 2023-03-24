package io.gardenerframework.camellia.authentication.server.administration.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhanghan30
 * @date 2023/3/24 17:34
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface UserAuthorizedOAuth2AuthorizationAdministrationComponent {
}
