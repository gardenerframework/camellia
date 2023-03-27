package io.gardenerframework.camellia.authentication.server.main.mfa.schema.request.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhanghan30
 * @date 2023/2/27 17:51
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = AuthenticatorNameSupportedValidator.class)
@Deprecated
public @interface AuthenticatorNameSupported {
    String message() default "will overwrite by validator";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
