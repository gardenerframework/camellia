package io.gardenerframework.camellia.authentication.server.main.spring;

import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEngineComponent;
import io.gardenerframework.camellia.authentication.server.main.UserAuthenticationService;
import io.gardenerframework.camellia.authentication.server.main.annotation.AuthenticationType;
import io.gardenerframework.camellia.authentication.server.main.event.schema.*;
import io.gardenerframework.camellia.authentication.server.main.event.support.AuthenticationEventBuilder;
import io.gardenerframework.camellia.authentication.server.main.exception.client.UserNotFoundException;
import io.gardenerframework.camellia.authentication.server.main.schema.LoginAuthenticationRequestToken;
import io.gardenerframework.camellia.authentication.server.main.schema.OAuth2ClientUserAuthenticationToken;
import io.gardenerframework.camellia.authentication.server.main.schema.UserAuthenticatedAuthentication;
import io.gardenerframework.camellia.authentication.server.main.schema.UserAuthenticationRequestToken;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.credentials.Credentials;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.credentials.PasswordCredentials;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.user.UserServiceDelegate;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import io.gardenerframework.fragrans.log.GenericBasicLogger;
import io.gardenerframework.fragrans.log.GenericOperationLogger;
import io.gardenerframework.fragrans.log.common.schema.reason.ExceptionCaught;
import io.gardenerframework.fragrans.log.common.schema.state.Failed;
import io.gardenerframework.fragrans.log.common.schema.verb.Process;
import io.gardenerframework.fragrans.log.common.schema.verb.Update;
import io.gardenerframework.fragrans.log.schema.content.GenericBasicLogContent;
import io.gardenerframework.fragrans.log.schema.content.GenericOperationLogContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.authorization.web.OAuth2TokenEndpointFilter;
import org.springframework.util.ClassUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * ???????????????????????????????????????
 * <p>
 * ???????????????????????????????????????
 *
 * @author ZhangHan
 * @date 2022/1/1 1:19
 * @see OAuth2TokenEndpointFilter
 * @see WebAuthenticationEntryProcessingFilter
 */
@Slf4j
@RequiredArgsConstructor
@AuthenticationServerEngineComponent
public class LoginAuthenticationRequestAuthenticator implements
        AuthenticationProvider, ApplicationEventPublisherAware,
        AuthenticationEventBuilder {
    private final UserServiceDelegate userService;
    private final AuthenticationEndpointExceptionAdapter authenticationEndpointExceptionAdapter;
    private final AuthenticationEndpointAuthenticatedAuthenticationAdapter authenticationEndpointAuthenticatedAuthenticationAdapter;
    private final GenericBasicLogger basicLogger;
    private final GenericOperationLogger operationLogger;
    private ApplicationEventPublisher eventPublisher;


    /**
     * ????????????
     *
     * @param authentication ????????????
     * @return ????????????
     * @throws Exception ??????????????????
     */
    private Authentication authenticateInternally(Authentication authentication) throws Exception {
        //??????????????????(?????????web??????token endpoint)
        LoginAuthenticationRequestToken loginAuthenticationRequestToken = (LoginAuthenticationRequestToken) authentication;
        UserAuthenticationRequestToken userAuthenticationRequestToken = loginAuthenticationRequestToken.getUserAuthenticationRequestToken();
        OAuth2ClientUserAuthenticationToken clientUserAuthenticationRequestToken = loginAuthenticationRequestToken.getClientUserAuthenticationRequestToken();
        String authenticationType = Objects.requireNonNull(AnnotationUtils.findAnnotation(ClassUtils.getUserClass(loginAuthenticationRequestToken.getContext().getUserAuthenticationService()), AuthenticationType.class)).value();
        //??????????????????????????????
        Principal principal = userAuthenticationRequestToken.getPrincipal();
        Credentials credentials = userAuthenticationRequestToken.getCredentials();
        //????????????????????????????????????
        UserAuthenticationService userAuthenticationService = loginAuthenticationRequestToken.getContext().getUserAuthenticationService();
        HttpServletRequest httpServletRequest = loginAuthenticationRequestToken.getContext().getHttpServletRequest();
        //??????????????????????????????
        OAuth2RequestingClient client = loginAuthenticationRequestToken.getContext().getClient();
        //?????????????????????
        Map<String, Object> context = loginAuthenticationRequestToken.getContext().getContext();
        //??????????????????
        User user = null;
        //?????????????????????????????????????????????????????????????????????????????????
        boolean userServiceCalled = false;
        try {
            //?????????????????????????????????
            if (clientUserAuthenticationRequestToken != null) {
                this.eventPublisher.publishEvent(buildAuthenticationEvent(
                        ClientAuthenticatedEvent.builder(),
                        httpServletRequest,
                        authenticationType,
                        principal,
                        client,
                        context).build());
                //??????????????????????????????????????????
                clientUserAuthenticationRequestToken.getPrincipal().setAuthenticated(true);
            }
            //?????????????????????
            eventPublisher.publishEvent(buildAuthenticationEvent(
                    UserAboutToLoadEvent.builder(),
                    httpServletRequest,
                    authenticationType,
                    principal,
                    client,
                    context).build());
            if (credentials instanceof PasswordCredentials) {
                //??????????????????????????????
                user = userService.authenticate(principal, (PasswordCredentials) credentials, context);
            } else {
                user = userService.load(principal, context);
            }
            //??????????????????????????????????????????????????????
            userServiceCalled = true;
            //??????????????????????????????????????????????????????
            if (user == null) {
                throw new UserNotFoundException(principal.getName());
            }
            //??????hold????????????
            Collection<Credentials> credentialsHolder = user.getCredentials();
            user.eraseCredentials();
            //??????????????????????????????????????????????????????????????????
            this.eventPublisher.publishEvent(buildAuthenticationEvent(
                    UserLoadedEvent.builder(),
                    httpServletRequest,
                    authenticationType,
                    principal,
                    client,
                    context
            ).user(user).build());
            user.setCredentials(credentialsHolder);
            //?????????????????????????????????????????????
            userAuthenticationService.authenticate(
                    userAuthenticationRequestToken,
                    client,
                    user,
                    context
            );
            //????????????????????????????????????????????????????????????
            user.eraseCredentials();
            //?????????????????????
            //??????????????????????????????????????????
            //??????mfa?????????
            //???????????????????????????
            //todo ??????mfa????????????????????????????????????????????????????????????????????????principal????????????
            this.eventPublisher.publishEvent(buildAuthenticationEvent(
                    UserAuthenticatedEvent.builder(),
                    httpServletRequest,
                    authenticationType,
                    principal,
                    client,
                    context
            ).user(user).build());
        } catch (AuthenticationException exception) {
            try {
                //????????????????????????
                //?????????????????????????????????????????????????????????????????????????????????????????????????????????
                //?????????????????????????????????????????????????????????????????????
                if (user == null && !userServiceCalled) {
                    //?????????????????????????????????????????????????????????
                    user = userService.load(principal, context);
                    //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                    if (user != null) {
                        user.eraseCredentials();
                    }
                }
            } catch (Exception e) {
                //???????????????????????????????????????????????????????????????????????????
                operationLogger.debug(
                        log,
                        GenericOperationLogContent.builder()
                                .what(User.class)
                                .operation(new Update())
                                .state(new Failed())
                                .build(),
                        e
                );
            }
            //????????????????????????????????????
            eventPublisher.publishEvent(buildAuthenticationEvent(
                    AuthenticationFailedEvent.builder(),
                    httpServletRequest,
                    authenticationType,
                    principal,
                    client,
                    context
            ).user(user).exception(exception).build());
            throw exception;
        }
        //todo ???????????????
        //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        try {
            this.eventPublisher.publishEvent(buildAuthenticationEvent(
                    AuthenticationSuccessEvent.builder(),
                    httpServletRequest,
                    authenticationType,
                    principal,
                    client,
                    context
            ).user(user).build());
        } catch (AuthenticationException exception) {
            //???????????????
            basicLogger.debug(
                    log,
                    GenericBasicLogContent.builder().
                            what(LoginAuthenticationRequestAuthenticator.class)
                            .how(new ExceptionCaught()).build(),
                    exception
            );
        }
        //?????????mfa??????????????????????????????????????????????????????????????????????????????oauth2????????????????????????????????????
        return authenticationEndpointAuthenticatedAuthenticationAdapter.adapt(
                httpServletRequest,
                new UserAuthenticatedAuthentication(user),
                clientUserAuthenticationRequestToken
        );
    }

    /**
     * ??????????????????
     *
     * @param request                         ??????
     * @param loginAuthenticationRequestToken ???????????????
     * @param exception                       ??????
     * @throws AuthenticationException ?????????????????????
     */
    private void onAuthenticationFailed(HttpServletRequest request, LoginAuthenticationRequestToken loginAuthenticationRequestToken, AuthenticationException exception) throws AuthenticationException {
        //???????????????????????????????????????
        operationLogger.debug(
                log,
                GenericOperationLogContent.builder()
                        .what(LoginAuthenticationRequestToken.class)
                        .operation(new Process())
                        .state(new Failed()).build(),
                exception
        );
        //???oauth2??????????????????oauth2???????????????
        throw authenticationEndpointExceptionAdapter.adapt(request, exception);
    }

    /**
     * ????????????????????????
     *
     * @param request                         ??????
     * @param loginAuthenticationRequestToken ?????????????????????
     * @param exception                       ??????
     */
    private void onAuthenticationServiceFailed(HttpServletRequest request, LoginAuthenticationRequestToken loginAuthenticationRequestToken, Exception exception) {
        //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        operationLogger.error(
                log,
                GenericOperationLogContent.builder()
                        .what(LoginAuthenticationRequestToken.class)
                        .operation(new Process())
                        .state(new Failed()).build(),
                exception
        );
        throw authenticationEndpointExceptionAdapter.adapt(
                request,
                new InternalAuthenticationServiceException(
                        exception.getMessage(),
                        exception
                )
        );
    }

    /**
     * ????????????
     *
     * @param request                         ??????
     * @param loginAuthenticationRequestToken ???????????????
     * @param userAuthenticatedAuthentication ?????????????????????
     */
    private void onAuthenticationSuccess(
            HttpServletRequest request,
            LoginAuthenticationRequestToken loginAuthenticationRequestToken,
            Authentication userAuthenticatedAuthentication
    ) {
        //todo ??????????????????????????????
    }

    /**
     * ????????????
     *
     * @param authentication ????????????
     * @return ????????????
     * @throws AuthenticationException ????????????
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Authentication authenticated = null;
        LoginAuthenticationRequestToken loginAuthenticationRequestToken = (LoginAuthenticationRequestToken) authentication;
        HttpServletRequest httpServletRequest = loginAuthenticationRequestToken.getContext().getHttpServletRequest();
        try {
            authenticated = authenticateInternally(authentication);
            onAuthenticationSuccess(httpServletRequest, loginAuthenticationRequestToken, authenticated);
        } catch (AuthenticationException exception) {
            onAuthenticationFailed(httpServletRequest, loginAuthenticationRequestToken, exception);
        } catch (Exception exception) {
            onAuthenticationServiceFailed(httpServletRequest, loginAuthenticationRequestToken, exception);
        }
        return authenticated;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return LoginAuthenticationRequestToken.class.isAssignableFrom(authentication);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }
}
