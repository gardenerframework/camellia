package io.gardenerframework.camellia.authentication.server.main.schema.request.constraints;

import io.gardenerframework.camellia.authentication.server.main.utils.UserAuthenticationServiceRegistry;
import io.gardenerframework.camellia.authentication.server.main.annotation.AuthenticationEndpoint;
import io.gardenerframework.camellia.authentication.server.utils.AuthenticationEndpointMatcher;
import io.gardenerframework.fragrans.validation.constraints.AbstractConstraintValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidatorContext;
import java.util.*;

/**
 * @author ZhangHan
 * @date 2022/5/11 12:29
 */
@Slf4j
public class AuthenticationTypeSupportedValidator extends AbstractConstraintValidator<AuthenticationTypeSupported, String> {
    @Autowired
    private AuthenticationEndpointMatcher authenticationEndpointMatcher;
    @Autowired
    private UserAuthenticationServiceRegistry registry;

    /**
     * 判断是否能转换当前认证入口
     *
     * @param request    http 请求
     * @param annotation 配置的注解
     * @return 是否
     */
    private boolean isAuthenticationEndpointSupported(HttpServletRequest request, @Nullable AuthenticationEndpoint annotation) {
        List<AuthenticationEndpoint.Endpoint> supportedEndpoint = new LinkedList<>();
        if (annotation == null) {
            supportedEndpoint.addAll(Arrays.asList(AuthenticationEndpoint.Endpoint.values()));
        } else {
            supportedEndpoint.addAll(Arrays.asList(annotation.value()));
        }
        if (authenticationEndpointMatcher.isTokenEndpoint(request)) {
            return supportedEndpoint.contains(AuthenticationEndpoint.Endpoint.OAUTH2);
        }
        if (authenticationEndpointMatcher.isWebAuthenticationEndpoint(request)) {
            return supportedEndpoint.contains(AuthenticationEndpoint.Endpoint.WEB);
        }
        return false;
    }

    @Override
    protected boolean validate(String value, ConstraintValidatorContext context, Map<String, Object> data) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(String.format("{%s}", AuthenticationTypeSupported.class.getCanonicalName())).addConstraintViolation();
        if (StringUtils.hasText(value)) {
            if (!registry.getRegisteredAuthenticationTypes(true, false).contains(value)) {
                return false;
            }
            HttpServletRequest request =
                    ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                            .getRequest();
            if (!isAuthenticationEndpointSupported(request, Objects.requireNonNull(registry.getItem(value)).getAuthenticationEndpoint())) {
                return false;
            }
            return true;
        }
        return false;
    }
}
