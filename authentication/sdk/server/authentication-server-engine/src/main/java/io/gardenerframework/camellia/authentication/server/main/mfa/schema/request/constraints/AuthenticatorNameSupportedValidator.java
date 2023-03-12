package io.gardenerframework.camellia.authentication.server.main.mfa.schema.request.constraints;

import io.gardenerframework.camellia.authentication.server.main.mfa.utils.MfaAuthenticationChallengeResponseServiceRegistry;
import io.gardenerframework.fragrans.validation.constraints.AbstractConstraintValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidatorContext;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2023/2/27 17:52
 */
public class AuthenticatorNameSupportedValidator extends AbstractConstraintValidator<AuthenticatorNameSupported, String> {
    @Autowired
    private MfaAuthenticationChallengeResponseServiceRegistry registry;

    @Override
    protected boolean validate(String value, ConstraintValidatorContext context, Map<String, Object> data) {
        //不允许为空字符串
        if (!StringUtils.hasText(value)) {
            return false;
        }
        return registry.hasMfaAuthenticationChallengeResponseService(value);
    }
}
