package io.gardenerframework.camellia.authentication.server.main.spring.oauth2;

import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEngineComponent;
import io.gardenerframework.camellia.authentication.server.main.exception.client.BadAuthenticationRequestParameterException;
import io.gardenerframework.camellia.authentication.server.main.schema.request.OAuth2TokenParameter;
import io.gardenerframework.camellia.authentication.server.main.spring.AuthenticationEndpointExceptionAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author ZhangHan
 * @date 2022/5/21 10:52
 */
@AuthenticationServerEngineComponent
@RequiredArgsConstructor
public class TokenEndpointAuthenticationConverterDelegate implements AuthenticationConverter {
    private final Validator validator;
    private final AuthenticationEndpointExceptionAdapter authenticationEndpointExceptionAdapter;
    private final List<AuthenticationConverter> converters = new LinkedList<>();


    public void addConverter(AuthenticationConverter... converters) {
        this.converters.addAll(Arrays.asList(converters));
    }

    @Override
    public Authentication convert(HttpServletRequest request) {
        //把统一使用的参数提取并验证了
        OAuth2TokenParameter oAuth2TokenParameter = new OAuth2TokenParameter(request);
        Set<ConstraintViolation<Object>> violations = validator.validate(oAuth2TokenParameter);
        if (!CollectionUtils.isEmpty(violations)) {
            //抛出适配后的异常
            throw authenticationEndpointExceptionAdapter.adapt(request, new BadAuthenticationRequestParameterException(violations));
        }
        //放到request的属性中
        request.setAttribute(OAuth2TokenParameter.class.getName(), oAuth2TokenParameter);
        //执行转换逻辑
        for (AuthenticationConverter converter : converters) {
            Authentication authentication = converter.convert(request);
            if (authentication != null) {
                return authentication;
            }
        }
        return null;
    }
}
