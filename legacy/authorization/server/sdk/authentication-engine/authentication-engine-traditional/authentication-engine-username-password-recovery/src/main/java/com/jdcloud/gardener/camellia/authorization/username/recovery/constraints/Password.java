package com.jdcloud.gardener.camellia.authorization.username.recovery.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 密码强度验证
 *
 * @author zhanghan30
 * @date 2022/7/20 11:15 上午
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Password {
}
