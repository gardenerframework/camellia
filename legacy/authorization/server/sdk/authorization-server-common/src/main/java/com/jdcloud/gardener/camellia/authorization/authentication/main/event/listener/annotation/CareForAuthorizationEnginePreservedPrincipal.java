package com.jdcloud.gardener.camellia.authorization.authentication.main.event.listener.annotation;

import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 代表事件监听者关系带有@CamelliaPreserved注解的{@link BasicPrincipal}
 * <p>
 * 事件中的登录请求类型符合时就会发送
 *
 * @author ZhangHan
 * @date 2022/4/27 23:36
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@CareForAuthorizationEnginePreservedObject
public @interface CareForAuthorizationEnginePreservedPrincipal {
}
