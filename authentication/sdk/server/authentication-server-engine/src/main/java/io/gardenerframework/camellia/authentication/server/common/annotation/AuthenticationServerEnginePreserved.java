package io.gardenerframework.camellia.authentication.server.common.annotation;

import io.gardenerframework.camellia.authentication.server.main.UserAuthenticationService;
import io.gardenerframework.camellia.authentication.server.main.user.UserService;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表明这是一个当前认证服务器核心组件预留的东西
 * <p>
 * 可以用来注解任何东西，比如{@link UserAuthenticationService}、{@link UserService}等等
 *
 * 这种东西就表达了是引擎内部使用，不是开发人员需要关注的内容
 *
 * @author ZhangHan
 * @date 2022/4/23 3:25
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthenticationServerEnginePreserved {
}
