package io.gardenerframework.camellia.authentication.server.main.spring;

import io.gardenerframework.camellia.authentication.server.main.UserAuthenticationService;
import io.gardenerframework.camellia.authentication.server.main.UserAuthenticationServiceRegistry;
import io.gardenerframework.camellia.authentication.server.main.schema.LoginAuthenticationRequestContext;
import io.gardenerframework.camellia.authentication.server.main.schema.LoginAuthenticationRequestToken;
import io.gardenerframework.camellia.authentication.server.main.schema.OAuth2ClientUserAuthenticationToken;
import io.gardenerframework.camellia.authentication.server.main.schema.UserAuthenticationRequestToken;
import io.gardenerframework.camellia.authentication.server.main.schema.request.AuthenticationRequestConstants;
import io.gardenerframework.camellia.authentication.server.main.schema.request.AuthenticationTypeParameter;
import io.gardenerframework.camellia.authentication.server.main.schema.request.OAuth2GrantTypeParameter;
import io.gardenerframework.camellia.authentication.server.main.schema.request.OAuth2ScopeParameter;
import io.gardenerframework.camellia.authentication.server.utils.AuthenticationEndpointMatcher;
import io.gardenerframework.fragrans.log.GenericBasicLogger;
import io.gardenerframework.fragrans.log.GenericOperationLogger;
import io.gardenerframework.fragrans.log.common.schema.reason.Mismatch;
import io.gardenerframework.fragrans.log.common.schema.state.Failed;
import io.gardenerframework.fragrans.log.common.schema.verb.Create;
import io.gardenerframework.fragrans.log.schema.content.GenericBasicLogContent;
import io.gardenerframework.fragrans.log.schema.content.GenericOperationLogContent;
import io.gardenerframework.fragrans.log.schema.details.Detail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.Assert;

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
@Slf4j
@RequiredArgsConstructor
public class LoginAuthenticationRequestConverter implements AuthenticationConverter {
    private final Validator validator;
    private final UserAuthenticationServiceRegistry userAuthenticationServiceRegistry;
    private final AuthenticationEndpointExceptionAdapter authenticationEndpointExceptionAdapter;
    private final AuthenticationEndpointMatcher authenticationEndpointMatcher;
    private final GenericBasicLogger basicLogger;
    private final GenericOperationLogger operationLogger;

    /**
     * 完成转换
     *
     * @param request http请求
     * @return 转换后的结果，固定为{@link LoginAuthenticationRequestToken}类型
     * @throws Exception 转换过程出现问题
     * @see LoginAuthenticationRequestToken
     */
    private LoginAuthenticationRequestToken doConvert(HttpServletRequest request) throws Exception {
        Set<ConstraintViolation<Object>> violations;
        OAuth2ClientUserAuthenticationToken clientUserAuthenticationRequestToken = null;
        if (authenticationEndpointMatcher.isTokenEndpoint(request)) {
            OAuth2GrantTypeParameter oAuth2GrantTypeParameter = new OAuth2GrantTypeParameter(request);
            oAuth2GrantTypeParameter.validate(validator);
            if (!Objects.equals(
                    AuthenticationRequestConstants.GrantTypes.USER_AUTHENTICATION,
                    oAuth2GrantTypeParameter.getGrantType())) {
                basicLogger.debug(
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
            OAuth2ScopeParameter oAuth2ScopeParameter = new OAuth2ScopeParameter(request);
            oAuth2ScopeParameter.validate(validator);
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
        authenticationTypeParameter.validate(validator);
        UserAuthenticationServiceRegistry.UserAuthenticationServiceRegistryItem userAuthenticationServiceRegistryItem = userAuthenticationServiceRegistry.getItem(authenticationTypeParameter.getAuthenticationType());
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
            operationLogger.debug(
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
}
