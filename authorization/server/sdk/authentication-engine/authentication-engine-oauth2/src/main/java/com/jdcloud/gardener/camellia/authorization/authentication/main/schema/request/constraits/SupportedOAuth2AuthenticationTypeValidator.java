package com.jdcloud.gardener.camellia.authorization.authentication.main.schema.request.constraits;

import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.OAuth2AuthenticationServiceRegistry;
import com.jdcloud.gardener.fragrans.validation.constraints.AbstractConstraintValidator;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidatorContext;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2022/11/9 16:30
 */
public class SupportedOAuth2AuthenticationTypeValidator extends AbstractConstraintValidator<SupportedOAuth2AuthenticationType, String> implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    private OAuth2AuthenticationServiceRegistry authenticationServiceRegistry;

    @Override
    public void initialize(SupportedOAuth2AuthenticationType constraintAnnotation) {
        authenticationServiceRegistry = applicationContext.getBean(OAuth2AuthenticationServiceRegistry.class);
    }

    @Override
    protected boolean validate(String value, ConstraintValidatorContext context, Map<String, Object> data) {
        if (!StringUtils.hasText(value)) {
            return true;
        }
        Object bean = authenticationServiceRegistry.getService(value);
        //当前bean是个sns认证服务的bean
        return bean != null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
