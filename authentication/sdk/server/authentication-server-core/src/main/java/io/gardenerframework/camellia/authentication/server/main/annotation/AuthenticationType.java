package io.gardenerframework.camellia.authentication.server.main.annotation;

import io.gardenerframework.camellia.authentication.server.main.UserAuthenticationService;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 被认证请求插件用来表达自己支持哪种认证请求
 *
 * @author zhanghan30
 * @date 2021/12/27 12:45 下午
 * @see UserAuthenticationService
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AuthenticationType {
    String value();
}
