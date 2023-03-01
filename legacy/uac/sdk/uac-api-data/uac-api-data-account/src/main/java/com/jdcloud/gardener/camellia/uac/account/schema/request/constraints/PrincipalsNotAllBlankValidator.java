package com.jdcloud.gardener.camellia.uac.account.schema.request.constraints;

import com.jdcloud.gardener.camellia.uac.account.schema.request.AuthenticateAccountParameterTemplate;
import com.jdcloud.gardener.fragrans.validation.constraints.AbstractConstraintValidator;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2022/9/22 12:22
 */
public class PrincipalsNotAllBlankValidator extends AbstractConstraintValidator<PrincipalsNotAllBlank, AuthenticateAccountParameterTemplate> {
    @Override
    protected boolean validate(AuthenticateAccountParameterTemplate value, ConstraintValidatorContext context, Map<String, Object> data) {
        for (String principal : Arrays.asList(
                value.getEmail(),
                value.getMobilePhoneNumber(),
                value.getUsername()
        )) {
            if (StringUtils.hasText(principal)) {
                return true;
            }
        }
        return false;
    }
}
