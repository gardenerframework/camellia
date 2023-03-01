package com.jdcloud.gardener.camellia.authorization.common.api.security;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解在接口上的注解，要求当前接口的所有方法客户端都需要持有 通过client_credentials换取的access token才能访问
 * <p>
 * 或者带着用户授权的token也行
 * <p>
 * 具体是要求客户端的token还是用户的token取决于api自己的要求
 * <p>
 * 在属性中，如果要求认证是强制的，则不需要接口进行什么处理，直接就会返回 403 错误
 * <p>
 * 此外，可以通过在安全防护层上进行构筑要求当前方法或接口修改为必须 保护
 *
 * @author ZhangHan
 * @date 2022/5/14 1:27
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessTokenProtectedEndpoint {
    @AliasFor("optional")
    boolean value() default false;

    @AliasFor("value")
    boolean optional() default false;
}
