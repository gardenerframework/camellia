package io.gardenerframework.camellia.authentication.server.main.schema.request.constraints;

import io.gardenerframework.camellia.authentication.server.main.UserAuthenticationServiceRegistry;
import io.gardenerframework.camellia.authentication.server.main.annotation.AuthenticationEndpoint;
import io.gardenerframework.camellia.authentication.server.utils.AuthenticationEndpointMatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author ZhangHan
 * @date 2022/5/11 12:29
 */
@Slf4j
public class AuthenticationTypeSupportedValidator implements ConstraintValidator<AuthenticationTypeSupported, String> {
    @Autowired
    private AuthenticationTypeRegistry visibleTypes;
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
    public boolean isValid(String value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(String.format("{%s}", AuthenticationTypeSupported.class.getCanonicalName())).addConstraintViolation();
        if (StringUtils.hasText(value)) {
            if (!visibleTypes.getTypes(true, false).contains(value)) {
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
