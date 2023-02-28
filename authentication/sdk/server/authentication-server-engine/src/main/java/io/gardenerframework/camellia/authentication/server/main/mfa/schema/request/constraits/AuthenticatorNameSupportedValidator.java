package io.gardenerframework.camellia.authentication.server.main.mfa.schema.request.constraits;

import io.gardenerframework.camellia.authentication.server.main.mfa.utils.MfaAuthenticationChallengeResponseServiceRegistry;
import io.gardenerframework.fragrans.validation.constraints.AbstractConstraintValidator;
import org.springframework.beans.factory.annotation.Autowired;

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
        MfaAuthenticationChallengeResponseServiceRegistry.MfaAuthenticationChallengeResponseServiceRegistryItem item = registry.getItem(value);
        return item != null && item.isEnabled();
    }
}
