package io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.request.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhanghan30
 * @date 2023/3/29 13:29
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Constraint(validatedBy = MfaAuthenticatorSupportedValidator.class)
public @interface MfaAuthenticatorSupported {
    String message() default "will overwrite by validator";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
