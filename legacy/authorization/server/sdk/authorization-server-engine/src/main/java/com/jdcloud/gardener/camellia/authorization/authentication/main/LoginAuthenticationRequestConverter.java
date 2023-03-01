package com.jdcloud.gardener.camellia.authorization.authentication.main;

import com.jdcloud.gardener.camellia.authorization.authentication.main.client.ClientGroupProvider;
import com.jdcloud.gardener.camellia.authorization.authentication.main.client.schema.PreservedGrantTypes;
import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.client.BadAuthenticationRequestParameterException;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.*;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.request.AuthenticationTypeParameter;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.request.OAuth2GrantTypeParameter;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.request.OAuth2ScopeParameter;
import com.jdcloud.gardener.camellia.authorization.authentication.utils.AuthenticationEndpointMatcher;
import com.jdcloud.gardener.fragrans.log.GenericLoggerStaticAccessor;
import com.jdcloud.gardener.fragrans.log.annotation.LogTarget;
import com.jdcloud.gardener.fragrans.log.common.schema.reason.Mismatch;
import com.jdcloud.gardener.fragrans.log.common.schema.state.Failed;
import com.jdcloud.gardener.fragrans.log.common.schema.verb.Create;
import com.jdcloud.gardener.fragrans.log.schema.content.GenericBasicLogContent;
import com.jdcloud.gardener.fragrans.log.schema.content.GenericOperationLogContent;
import com.jdcloud.gardener.fragrans.log.schema.details.Detail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 转换用户登录请求
 *
 * @author zhanghan30
 * @date 2022/4/19 6:48 下午
 */
@LogTarget("用户登录请求转换器")
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginAuthenticationRequestConverter implements AuthenticationConverter {
    private final Validator validator;
    private final UserAuthenticationServiceRegistry userAuthenticationServiceRegistry;
    private final AuthenticationEndpointExceptionAdapter authenticationEndpointExceptionAdapter;
    private final ClientGroupProvider clientGroupProvider;
    private final AuthenticationEndpointMatcher authenticationEndpointMatcher;

    /**
     * 完成转换
     *
     * @param request http请求
     * @return 转换后的结果，固定为{@link LoginAuthenticationRequestToken}类型
     * @see LoginAuthenticationRequestToken
     */
    private LoginAuthenticationRequestToken doConvert(HttpServletRequest request) {
        Set<ConstraintViolation<Object>> violations;
        OAuth2ClientUserAuthenticationToken clientUserAuthenticationRequestToken = null;
        if (authenticationEndpointMatcher.isTokenEndpoint(request)) {
            OAuth2GrantTypeParameter oAuth2GrantTypeParameter = new OAuth2GrantTypeParameter(request);
            violations = validator.validate(oAuth2GrantTypeParameter);
            if (!CollectionUtils.isEmpty(violations)) {
                throw new BadAuthenticationRequestParameterException(violations);
            }
            if (!Objects.equals(PreservedGrantTypes.USER_AUTHENTICATION, oAuth2GrantTypeParameter.getGrantType())) {
                GenericLoggerStaticAccessor.basicLogger().debug(
                        log,
                        GenericBasicLogContent.builder()
                                .what(OAuth2GrantTypeParameter.class)
                                .how(new Mismatch()).
                                detail(new Detail() {
                                    private final String grantType = oAuth2GrantTypeParameter.getGrantType();
                                }).build(),
                        null
                );
                //不需要转换什么
                return null;
            }
            violations = validator.validate(oAuth2GrantTypeParameter);
            if (!CollectionUtils.isEmpty(violations)) {
                throw new BadAuthenticationRequestParameterException(violations);
            }
            OAuth2ScopeParameter oAuth2ScopeParameter = new OAuth2ScopeParameter(request);
            violations = validator.validate(oAuth2ScopeParameter);
            if (!CollectionUtils.isEmpty(violations)) {
                throw new BadAuthenticationRequestParameterException(violations);
            }
            OAuth2ClientAuthenticationToken clientAuthentication = (OAuth2ClientAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            //生成客户端的用户认证请求
            clientUserAuthenticationRequestToken = new OAuth2ClientUserAuthenticationToken(
                    new AuthorizationGrantType(oAuth2GrantTypeParameter.getGrantType()),
                    clientAuthentication,
                    clientAuthentication.getAdditionalParameters(),
                    new HashSet<>(oAuth2ScopeParameter.getScopes())
            );
        }
        AuthenticationTypeParameter authenticationTypeParameter = new AuthenticationTypeParameter(request);
        violations = validator.validate(authenticationTypeParameter);
        if (!CollectionUtils.isEmpty(violations)) {
            throw new BadAuthenticationRequestParameterException(violations);
        }
        UserAuthenticationServiceRegistryItem userAuthenticationServiceRegistryItem = userAuthenticationServiceRegistry.getItem(authenticationTypeParameter.getAuthenticationType());
        //经过验证器验证过了，不需要再验证
        UserAuthenticationService service = Objects.requireNonNull(userAuthenticationServiceRegistryItem).getService();
        UserAuthenticationRequestToken userAuthenticationRequestToken = service.convert(request);
        Assert.notNull(userAuthenticationRequestToken, "service " + service.getClass().getCanonicalName() + " returned a null request");

        LoginAuthenticationRequestContext loginAuthenticationRequestContext = new LoginAuthenticationRequestContext(
                service,
                request
        );
        //创建认证请求上下文
        return new LoginAuthenticationRequestToken(
                userAuthenticationRequestToken,
                clientUserAuthenticationRequestToken,
                getClientGroup(clientUserAuthenticationRequestToken),
                loginAuthenticationRequestContext
        );
    }

    /**
     * 转换http请求为{@link LoginAuthenticationRequestToken}
     *
     * @param request http 请求
     * @return 用户认证请求
     */
    @Override
    public LoginAuthenticationRequestToken convert(HttpServletRequest request) {
        try {
            return doConvert(request);
        } catch (Exception exception) {
            GenericLoggerStaticAccessor.operationLogger().debug(
                    log,
                    GenericOperationLogContent.builder()
                            .what(LoginAuthenticationRequestToken.class)
                            .operation(new Create())
                            .state(new Failed()).build(),
                    exception
            );
            //这里是有必要转换的，因为这个转换器还工作在oauth2的token endpoint
            //哪个endpoint只管OAuth2AuthenticationException
            throw authenticationEndpointExceptionAdapter.adapt(
                    request,
                    exception instanceof AuthenticationException ?
                            (AuthenticationException) exception :
                            new InternalAuthenticationServiceException(exception.getMessage(), exception)
            );
        }
    }

    /**
     * 默认使用client id或"web"作为应用组
     *
     * @param clientUserAuthenticationRequestToken 客户端用户认证请求
     * @return 应用组
     */
    private String getClientGroup(@Nullable OAuth2ClientUserAuthenticationToken clientUserAuthenticationRequestToken) {
        return clientGroupProvider.getClientGroup(
                clientUserAuthenticationRequestToken == null ?
                        null : clientUserAuthenticationRequestToken.getRegisteredClient()
        );
    }
}
