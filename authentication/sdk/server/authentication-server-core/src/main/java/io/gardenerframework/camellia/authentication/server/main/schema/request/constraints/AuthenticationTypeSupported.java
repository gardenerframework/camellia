package io.gardenerframework.camellia.authentication.server.main.schema.request.constraints;

import io.gardenerframework.camellia.authentication.server.main.UserAuthenticationService;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 要求认证类型被支持
 *
 * @author ZhangHan
 * @date 2022/5/11 12:22
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = AuthenticationTypeSupportedValidator.class)
public @interface AuthenticationTypeSupported {
    /**
     * 要求参数对应的服务类
     *
     * @return 服务类
     */
    Class<? extends UserAuthenticationService> type() default UserAuthenticationService.class;

    /**
     * 参数是否指向一个引擎预留的类型
     *
     * @return 是否引擎预留
     */
    boolean ignorePreserved();

    /**
     * 当前是在什么类型的接口中
     *
     * @return 接口类型
     */
    EndpointType endpointType();

    String message() default "will overwrite by validator";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    enum EndpointType {
        /**
         * 当前是在rest api中进行验证
         */
        RestApi,
        /**
         * 当前是在认证接口进行验证
         */
        Authentication
    }
}
