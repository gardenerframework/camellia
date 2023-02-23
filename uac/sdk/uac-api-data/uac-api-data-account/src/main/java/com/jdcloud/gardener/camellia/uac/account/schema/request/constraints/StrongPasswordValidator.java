package com.jdcloud.gardener.camellia.uac.account.schema.request.constraints;

import com.jdcloud.gardener.fragrans.validation.constraints.AbstractConstraintValidator;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidatorContext;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2022/9/22 12:22
 */
@RequiredArgsConstructor
public class StrongPasswordValidator extends AbstractConstraintValidator<StrongPassword, CharSequence> {
    private final PasswordStrengthChecker strengthChecker;

    @Override
    protected boolean validate(CharSequence value, ConstraintValidatorContext context, Map<String, Object> data) {
        //这里是不关心没有传入的参数
        if (value == null) {
            return true;
        }
        return strengthChecker.check(value);
    }
}
