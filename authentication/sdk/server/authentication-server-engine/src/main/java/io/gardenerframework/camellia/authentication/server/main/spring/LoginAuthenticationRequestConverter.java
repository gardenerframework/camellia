package io.gardenerframework.camellia.authentication.server.main.spring;

import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEngineComponent;
import io.gardenerframework.camellia.authentication.server.main.UserAuthenticationService;
import io.gardenerframework.camellia.authentication.server.main.schema.LoginAuthenticationRequestContext;
import io.gardenerframework.camellia.authentication.server.main.schema.LoginAuthenticationRequestToken;
import io.gardenerframework.camellia.authentication.server.main.schema.OAuth2ClientUserAuthenticationToken;
import io.gardenerframework.camellia.authentication.server.main.schema.UserAuthenticationRequestToken;
import io.gardenerframework.camellia.authentication.server.main.schema.request.AuthenticationRequestConstants;
import io.gardenerframework.camellia.authentication.server.main.schema.request.AuthenticationTypeParameter;
import io.gardenerframework.camellia.authentication.server.main.schema.request.OAuth2GrantTypeParameter;
import io.gardenerframework.camellia.authentication.server.main.schema.request.OAuth2ScopeParameter;
import io.gardenerframework.camellia.authentication.server.main.utils.AuthenticationEndpointMatcher;
import io.gardenerframework.camellia.authentication.server.main.utils.UserAuthenticationServiceRegistry;
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
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

/**
 * ????????????????????????
 *
 * @author zhanghan30
 * @date 2022/4/19 6:48 ??????
 */
@Slf4j
@RequiredArgsConstructor
@AuthenticationServerEngineComponent
public class LoginAuthenticationRequestConverter implements AuthenticationConverter {
    private final Validator validator;
    private final UserAuthenticationServiceRegistry userAuthenticationServiceRegistry;
    private final AuthenticationEndpointExceptionAdapter authenticationEndpointExceptionAdapter;
    private final AuthenticationEndpointMatcher authenticationEndpointMatcher;
    private final GenericBasicLogger basicLogger;
    private final GenericOperationLogger operationLogger;

    /**
     * ????????????
     *
     * @param request http??????
     * @return ??????????????????????????????{@link LoginAuthenticationRequestToken}??????
     * @throws Exception ????????????????????????
     * @see LoginAuthenticationRequestToken
     */
    private LoginAuthenticationRequestToken doConvert(HttpServletRequest request) throws Exception {
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
                //?????????????????????
                return null;
            }
            OAuth2ScopeParameter oAuth2ScopeParameter = new OAuth2ScopeParameter(request);
            oAuth2ScopeParameter.validate(validator);
            OAuth2ClientAuthenticationToken clientAuthentication = (OAuth2ClientAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            //????????????????????????????????????
            clientUserAuthenticationRequestToken = new OAuth2ClientUserAuthenticationToken(
                    new AuthorizationGrantType(oAuth2GrantTypeParameter.getGrantType()),
                    clientAuthentication,
                    clientAuthentication.getAdditionalParameters(),
                    new HashSet<>(oAuth2ScopeParameter.getScopes())
            );
        }
        AuthenticationTypeParameter authenticationTypeParameter = new AuthenticationTypeParameter(request);
        authenticationTypeParameter.validate(validator);
        //????????????????????????????????????????????????
        //???????????????????????????
        UserAuthenticationService service = userAuthenticationServiceRegistry.getUserAuthenticationService(authenticationTypeParameter.getAuthenticationType(), false);
        //?????????????????????
        OAuth2RequestingClient client = clientUserAuthenticationRequestToken == null ?
                null : OAuth2RequestingClient.builder()
                .clientId(clientUserAuthenticationRequestToken.getClientId())
                .grantType(clientUserAuthenticationRequestToken.getGrantType().getValue())
                .scopes(clientUserAuthenticationRequestToken.getScopes())
                .build();
        //??????????????????????????????
        Map<String, Object> context = new HashMap<>(10);
        //??????????????????????????????
        context.put(RegisteredClient.class.getName(),
                clientUserAuthenticationRequestToken == null ? null :
                        clientUserAuthenticationRequestToken.getRegisteredClient()
        );
        UserAuthenticationRequestToken userAuthenticationRequestToken = service.convert(
                request,
                client,
                context
        );
        Assert.notNull(userAuthenticationRequestToken, "service " + service.getClass().getCanonicalName() + " returned a null request");
        LoginAuthenticationRequestContext loginAuthenticationRequestContext = new LoginAuthenticationRequestContext(
                service,
                request,
                client,
                context
        );
        //???????????????????????????
        return new LoginAuthenticationRequestToken(
                userAuthenticationRequestToken,
                clientUserAuthenticationRequestToken,
                loginAuthenticationRequestContext
        );
    }

    /**
     * ??????http?????????{@link LoginAuthenticationRequestToken}
     *
     * @param request http ??????
     * @return ??????????????????
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
            //???????????????????????????????????????????????????????????????oauth2???token endpoint
            //??????endpoint??????OAuth2AuthenticationException
            throw authenticationEndpointExceptionAdapter.adapt(
                    request,
                    exception instanceof AuthenticationException ?
                            (AuthenticationException) exception :
                            new InternalAuthenticationServiceException(exception.getMessage(), exception)
            );
        }
    }
}
