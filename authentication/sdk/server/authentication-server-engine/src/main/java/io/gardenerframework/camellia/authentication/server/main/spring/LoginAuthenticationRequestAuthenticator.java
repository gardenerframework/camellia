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
 * 统一页面用的登录请求处理器
 * <p>
 * 真正执行认证逻辑的幕后黑手
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
     * 执行逻辑
     *
     * @param authentication 认证请求
     * @return 认证结果
     * @throws Exception 发生任何异常
     */
    private Authentication authenticateInternally(Authentication authentication) throws Exception {
        //读取登录请求(无论是web还是token endpoint)
        LoginAuthenticationRequestToken loginAuthenticationRequestToken = (LoginAuthenticationRequestToken) authentication;
        UserAuthenticationRequestToken userAuthenticationRequestToken = loginAuthenticationRequestToken.getUserAuthenticationRequestToken();
        OAuth2ClientUserAuthenticationToken clientUserAuthenticationRequestToken = loginAuthenticationRequestToken.getClientUserAuthenticationRequestToken();
        String authenticationType = Objects.requireNonNull(AnnotationUtils.findAnnotation(ClassUtils.getUserClass(loginAuthenticationRequestToken.getContext().getUserAuthenticationService()), AuthenticationType.class)).value();
        //当前登录使用的登录名
        Principal principal = userAuthenticationRequestToken.getPrincipal();
        Credentials credentials = userAuthenticationRequestToken.getCredentials();
        //从上下文中取出其它的东西
        UserAuthenticationService userAuthenticationService = loginAuthenticationRequestToken.getContext().getUserAuthenticationService();
        HttpServletRequest httpServletRequest = loginAuthenticationRequestToken.getContext().getHttpServletRequest();
        //设置当前登录的客户端
        OAuth2RequestingClient client = loginAuthenticationRequestToken.getContext().getClient();
        //生成验证上下文
        Map<String, Object> context = loginAuthenticationRequestToken.getContext().getContext();
        //准备读取用户
        User user = null;
        //标识位，用来标记用户服务是否没有受到任何异常的被调用了
        boolean userServiceCalled = false;
        try {
            //第一步，开始认证客户端
            if (clientUserAuthenticationRequestToken != null) {
                this.eventPublisher.publishEvent(buildAuthenticationEvent(
                        ClientAuthenticatedEvent.builder(),
                        httpServletRequest,
                        authenticationType,
                        principal,
                        client,
                        context).build());
                //没有问题要标记客户端认证完成
                clientUserAuthenticationRequestToken.getPrincipal().setAuthenticated(true);
            }
            //发布加载前事件
            eventPublisher.publishEvent(buildAuthenticationEvent(
                    UserAboutToLoadEvent.builder(),
                    httpServletRequest,
                    authenticationType,
                    principal,
                    client,
                    context).build());
            if (credentials instanceof PasswordCredentials) {
                //密码类型的走认证接口
                user = userService.authenticate(principal, (PasswordCredentials) credentials, context);
            } else {
                user = userService.load(principal, context);
            }
            //到此，用户服务没有任何异常地调用完了
            userServiceCalled = true;
            //没有加载出来用户那肯定就是不用继续了
            if (user == null) {
                throw new UserNotFoundException(principal.getName());
            }
            //临时hold一下密码
            Collection<Credentials> credentialsHolder = user.getCredentials();
            user.eraseCredentials();
            //用户加载事件不需要密码，以防错误的日志打印等
            this.eventPublisher.publishEvent(buildAuthenticationEvent(
                    UserLoadedEvent.builder(),
                    httpServletRequest,
                    authenticationType,
                    principal,
                    client,
                    context
            ).user(user).build());
            user.setCredentials(credentialsHolder);
            //现在是任何类型的认证都需要认证
            userAuthenticationService.authenticate(
                    userAuthenticationRequestToken,
                    client,
                    user,
                    context
            );
            //用户认证已经完成，读取出的用户不需要密码
            user.eraseCredentials();
            //发送认证后事件
            //在这里觉得应该阻断登录就阻断
            //比如mfa还没做
            //比如账号的状态不对
            //todo 解决mfa认证通过后，正常监听器无法收到实践的问题，原因是principal是预留的
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
                //尝试补偿一下用户
                //当且仅当用户为空不是因为用户服务加载完了之后没有返回任何用户信息导致的
                //不然的话也就不需要补偿了，因为补偿完了也是空的
                if (user == null && !userServiceCalled) {
                    //这里，无论是什么异常，下面都进行了捕捉
                    user = userService.load(principal, context);
                    //但也有可能是因为还没有开始加载用户前就抛异常了，因此即使补偿了也可能还是空的
                    if (user != null) {
                        user.eraseCredentials();
                    }
                }
            } catch (Exception e) {
                //但发生问题就算了，也不要把这个问题抛出，保持主逻辑
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
            //在这个阶段发生了认证异常
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
        //todo 管理登录态
        //用户加载成功了、状态验证了、访问上下文也有了，且插件认为已经有了其它上下文也无所谓，没有什么好检查得了
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
            //记录个日志
            basicLogger.debug(
                    log,
                    GenericBasicLogContent.builder().
                            what(LoginAuthenticationRequestAuthenticator.class)
                            .how(new ExceptionCaught()).build(),
                    exception
            );
        }
        //不需要mfa则才是真认证完了，最终处理一下认证完成的对象，如果是oauth2的地址，则要转成授权令牌
        return authenticationEndpointAuthenticatedAuthenticationAdapter.adapt(
                httpServletRequest,
                new UserAuthenticatedAuthentication(user),
                clientUserAuthenticationRequestToken
        );
    }

    /**
     * 处理认证失败
     *
     * @param request                         请求
     * @param loginAuthenticationRequestToken 认证上下文
     * @param exception                       异常
     * @throws AuthenticationException 如果要阻断认证
     */
    private void onAuthenticationFailed(HttpServletRequest request, LoginAuthenticationRequestToken loginAuthenticationRequestToken, AuthenticationException exception) throws AuthenticationException {
        //认证发生问题，先记录个日志
        operationLogger.debug(
                log,
                GenericOperationLogContent.builder()
                        .what(LoginAuthenticationRequestToken.class)
                        .operation(new Process())
                        .state(new Failed()).build(),
                exception
        );
        //在oauth2的端点要转成oauth2的基础异常
        throw authenticationEndpointExceptionAdapter.adapt(request, exception);
    }

    /**
     * 认证服务出现异常
     *
     * @param request                         请求
     * @param loginAuthenticationRequestToken 认证请求上下文
     * @param exception                       异常
     */
    private void onAuthenticationServiceFailed(HttpServletRequest request, LoginAuthenticationRequestToken loginAuthenticationRequestToken, Exception exception) {
        //对于其它类型的异常，那肯定是哪里出问题了，直接认为认证失败即可，需要转为认证错误，否则弹出白色的页面
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
     * 认证成功
     *
     * @param request                         请求
     * @param loginAuthenticationRequestToken 认证上下文
     * @param userAuthenticatedAuthentication 认证出来的对象
     */
    private void onAuthenticationSuccess(
            HttpServletRequest request,
            LoginAuthenticationRequestToken loginAuthenticationRequestToken,
            Authentication userAuthenticatedAuthentication
    ) {
        //todo 发送登录成功领域事件
    }

    /**
     * 执行认证
     *
     * @param authentication 认证请求
     * @return 认证结果
     * @throws AuthenticationException 认证失败
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
