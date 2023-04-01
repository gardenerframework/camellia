package io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.request.constraints;


import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.utils.MfaAuthenticatorRegistry;
import io.gardenerframework.fragrans.validation.constraints.AbstractConstraintValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidatorContext;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2023/2/27 17:52
 */
public class MfaAuthenticatorSupportedValidator extends AbstractConstraintValidator<MfaAuthenticatorSupported, String> {
    @Autowired
    private MfaAuthenticatorRegistry registry;

    @Override
    protected boolean validate(String value, ConstraintValidatorContext context, Map<String, Object> data) {
        //不允许为空字符串
        if (!StringUtils.hasText(value)) {
            return false;
        }
        return registry.getAuthenticator(value) != null;
    }
}
