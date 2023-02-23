package com.jdcloud.gardener.camellia.authorization.authentication.main.event.listener.annotation;

import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;
import org.springframework.security.core.AuthenticationException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 代表事件监听者关系带有@CamelliaPreserved注解的玩意
 * <p>
 * 目前已知的保留对象有{@link BasicPrincipal}(比如带有挑战id的mfa登录类型）和{@link AuthenticationException}，比如非法的mfa认证请求错误等
 * <p>
 * 开发人员关心这类东西产生的事件再监听
 *
 * @author ZhangHan
 * @date 2022/4/27 23:36
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CareForAuthorizationEnginePreservedObject {
}
