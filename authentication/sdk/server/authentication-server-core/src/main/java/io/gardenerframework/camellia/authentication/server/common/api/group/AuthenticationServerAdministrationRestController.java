package io.gardenerframework.camellia.authentication.server.common.api.group;

import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表达是在认证服务器上用于管理的接口
 *
 * @author ZhangHan
 * @date 2022/5/11 11:25
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@RestController
public @interface AuthenticationServerAdministrationRestController {
}
