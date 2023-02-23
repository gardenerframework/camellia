package io.gardenerframework.camellia.authentication.server.main.schema.request.constraints;

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
@Target(ElementType.FIELD)
@Constraint(validatedBy = AuthenticationTypeSupportedValidator.class)
public @interface AuthenticationTypeSupported {
    String message() default "will overwrite by validator";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
