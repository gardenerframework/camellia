package com.jdcloud.gardener.camellia.authorization.challenge.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 挑战已被应答
 *
 * @author zhanghan30
 * @date 2022/5/23 8:01 下午
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateChallenge {
}
