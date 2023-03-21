package io.gardenerframework.camellia.authentication.server.common.api.group;

import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 认证服务器管理用接口，可分别用于认证服务器和管理后台
 *
 * @author ZhangHan
 * @date 2022/5/11 11:25
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@RestController
public @interface AuthenticationServerAdministrationController {
}
