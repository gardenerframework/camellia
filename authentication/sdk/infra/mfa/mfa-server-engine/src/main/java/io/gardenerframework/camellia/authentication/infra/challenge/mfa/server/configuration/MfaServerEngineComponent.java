package io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhanghan30
 * @date 2023/3/29 14:28
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MfaServerEngineComponent {
}
