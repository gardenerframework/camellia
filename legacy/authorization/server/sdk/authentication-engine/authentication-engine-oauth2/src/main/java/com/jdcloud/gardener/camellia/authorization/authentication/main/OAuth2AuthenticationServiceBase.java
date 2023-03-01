package com.jdcloud.gardener.camellia.authorization.authentication.main;

import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.client.BadAuthenticationRequestParameterException;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.UserAuthenticationRequestToken;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.request.OAuth2AuthorizationCodeParameter;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Objects;
import java.util.Set;

/**
 * @author ZhangHan
 * @date 2022/11/8 20:37
 */
@RequiredArgsConstructor
public abstract class OAuth2AuthenticationServiceBase implements UserAuthenticationService {
    private final Validator validator;
    private final OAuth2StateService oAuth2StateService;

    @Override
    public UserAuthenticationRequestToken convert(HttpServletRequest request) throws AuthenticationException {
        OAuth2AuthorizationCodeParameter parameter = new OAuth2AuthorizationCodeParameter(request);
        Set<ConstraintViolation<Object>> violations = validator.validate(parameter);
        if (!CollectionUtils.isEmpty(violations)) {
            throw new BadAuthenticationRequestParameterException(violations);
        }
        //执行csrf检查
        oAuth2StateService.validate(this, request, parameter.getState());
        //加载sns用户信息
        return new UserAuthenticationRequestToken(
                Objects.requireNonNull(loadSnsUser(parameter.getCode()))
        );
    }

    /**
     * 通过授权码去加载用户，并给出当前用户的登录名
     *
     * @param code 授权码
     * @return 登录名
     * @throws AuthenticationException 加载有问题抛出异常，而不要返回null的登录名
     */
    protected abstract BasicPrincipal loadSnsUser(String code) throws AuthenticationException;

    @Override
    public void authenticate(UserAuthenticationRequestToken authenticationRequest, User user) throws AuthenticationException {
        //不需要做什么验证
    }
}
