package io.gardenerframework.camellia.authentication.server.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 管理服务器bean注解
 *
 * @author zhanghan30
 * @date 2023/3/22 16:56
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AdministrationServerComponent {
}
