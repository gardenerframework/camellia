package io.gardenerframework.camellia.authentication.server.main.schema.request.constraints;

import io.gardenerframework.camellia.authentication.server.main.UserAuthenticationService;
import io.gardenerframework.camellia.authentication.server.main.annotation.SupportAuthenticationEndpoint;
import io.gardenerframework.camellia.authentication.server.main.utils.AuthenticationEndpointMatcher;
import io.gardenerframework.camellia.authentication.server.main.utils.UserAuthenticationServiceRegistry;
import io.gardenerframework.fragrans.validation.constraints.AbstractConstraintValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
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
    private AuthenticationEndpointMatcher matcher;
    @Autowired
    private UserAuthenticationServiceRegistry registry;
    /**
     * 实际注解
     */
    private AuthenticationTypeSupported constraintAnnotation;

    @Override
    public void initialize(AuthenticationTypeSupported constraintAnnotation) {
        this.constraintAnnotation = constraintAnnotation;
    }

    /**
     * 判断是否能转换当前认证入口
     *
     * @param request    http 请求
     * @param annotation 配置的注解
     * @return 是否
     */
    private boolean isAuthenticationEndpointSupported(HttpServletRequest request, @Nullable SupportAuthenticationEndpoint annotation) {
        List<SupportAuthenticationEndpoint.Endpoint> supportedEndpoint = new LinkedList<>();
        if (annotation == null) {
            supportedEndpoint.addAll(Arrays.asList(SupportAuthenticationEndpoint.Endpoint.values()));
        } else {
            supportedEndpoint.addAll(Arrays.asList(annotation.value()));
        }
        if (matcher.isTokenEndpoint(request)) {
            return supportedEndpoint.contains(SupportAuthenticationEndpoint.Endpoint.OAUTH2);
        }
        if (matcher.isWebAuthenticationEndpoint(request)) {
            return supportedEndpoint.contains(SupportAuthenticationEndpoint.Endpoint.WEB);
        }
        return false;
    }

    @Override
    protected boolean validate(String value, ConstraintValidatorContext context, Map<String, Object> data) {
        if (StringUtils.hasText(value)) {
            //获取需要的类型
            Class<? extends UserAuthenticationService> requiredType = constraintAnnotation.type();
            //获取是否需要忽略引擎保留的服务
            boolean ignorePreserved = constraintAnnotation.ignorePreserved();
            UserAuthenticationService userAuthenticationService = registry.getUserAuthenticationService(value, ignorePreserved);
            if (userAuthenticationService == null || !requiredType.isAssignableFrom(userAuthenticationService.getClass())) {
                //没有对应的服务或者类型不匹配
                return false;
            }
            HttpServletRequest request =
                    ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                            .getRequest();
            return !AuthenticationTypeSupported.EndpointType.Authentication.equals(this.constraintAnnotation.endpointType()) || isAuthenticationEndpointSupported(
                    request,
                    AnnotationUtils.findAnnotation(userAuthenticationService.getClass(), SupportAuthenticationEndpoint.class)
            );
        }
        return false;
    }

    @Nullable
    @Override
    protected Map<String, Object> getMessageParameters(String value, Map<String, Object> data) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", value);
        return parameters;
    }
}
