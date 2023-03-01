package com.jdcloud.gardener.camellia.authorization.authentication.main.schema.request.constraits;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhanghan30
 * @date 2022/11/9 16:27
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = SupportedOAuth2AuthenticationTypeValidator.class)
public @interface SupportedOAuth2AuthenticationType {
    String message() default "will overwrite by validator";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
