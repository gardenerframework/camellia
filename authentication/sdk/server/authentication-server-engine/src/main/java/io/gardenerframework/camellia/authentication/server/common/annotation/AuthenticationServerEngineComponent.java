package io.gardenerframework.camellia.authentication.server.common.annotation;

import org.springframework.context.annotation.ComponentScan;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 作用于{@link ComponentScan}注解，表达这是引擎的bean，否则bean太多了
 *
 * @author zhanghan30
 * @date 2023/2/28 13:49
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AuthenticationServerEngineComponent {
}
