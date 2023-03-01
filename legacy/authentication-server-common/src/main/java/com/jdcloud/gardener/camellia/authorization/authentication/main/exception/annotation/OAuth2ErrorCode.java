package com.jdcloud.gardener.camellia.authorization.authentication.main.exception.annotation;

import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.OAuth2ErrorCodes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解在异常的东西，指示当前异常应当转为什么oauth2的编码
 * <p>
 * 此外状态码通过@ResponseStatus去表达
 *
 * @author zhanghan30
 * @date 2022/4/20 6:30 下午
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface OAuth2ErrorCode {
    String value() default OAuth2ErrorCodes.SERVER_ERROR;
}
