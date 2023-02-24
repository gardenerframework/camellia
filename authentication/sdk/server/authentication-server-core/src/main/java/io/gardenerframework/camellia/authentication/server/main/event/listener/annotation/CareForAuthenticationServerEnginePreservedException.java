package io.gardenerframework.camellia.authentication.server.main.event.listener.annotation;

import org.springframework.security.core.AuthenticationException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 代表事件监听者关系带有@CamelliaPreserved注解的{@link AuthenticationException}
 * <p>
 * 事件中的异常符合时就会发送
 *
 * @author ZhangHan
 * @date 2022/4/27 23:36
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@CareForAuthenticationServerEnginePreservedObject
public @interface CareForAuthenticationServerEnginePreservedException {
}
