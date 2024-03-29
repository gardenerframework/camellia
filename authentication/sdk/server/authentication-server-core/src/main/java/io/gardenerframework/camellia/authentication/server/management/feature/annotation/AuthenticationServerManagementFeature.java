package io.gardenerframework.camellia.authentication.server.management.feature.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhanghan30
 * @date 2023/3/20 18:52
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AuthenticationServerManagementFeature {
    String value();
}
