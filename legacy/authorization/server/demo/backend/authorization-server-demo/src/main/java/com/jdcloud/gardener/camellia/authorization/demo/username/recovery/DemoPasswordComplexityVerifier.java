package com.jdcloud.gardener.camellia.authorization.demo.username.recovery;

import com.jdcloud.gardener.camellia.authorization.username.recovery.constraints.AbstractPasswordValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidatorContext;
import java.util.Map;

/**
 * @author ZhangHan
 * @date 2022/5/16 7:10
 */
@Component
@ConditionalOnClass(AbstractPasswordValidator.class)
public class DemoPasswordComplexityVerifier extends AbstractPasswordValidator {

    @Override
    protected boolean validate(String value, ConstraintValidatorContext context, Map<String, Object> data) {
        return true;
    }
}
