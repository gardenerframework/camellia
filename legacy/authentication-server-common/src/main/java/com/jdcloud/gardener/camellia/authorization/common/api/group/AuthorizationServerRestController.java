package com.jdcloud.gardener.camellia.authorization.common.api.group;

import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 配合 api-group用的，给当前认证服务器的所有rest接口分分成1组
 *
 * @author ZhangHan
 * @date 2022/5/11 11:25
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@RestController
public @interface AuthorizationServerRestController {
}
