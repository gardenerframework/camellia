package com.jdcloud.gardener.camellia.authorization.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表明这是一个当前认证服务器核心组件预留的东西
 * <p>
 * 不是交给插件实现的
 *
 * @author ZhangHan
 * @date 2022/4/23 3:25
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthorizationEnginePreserved {
}
