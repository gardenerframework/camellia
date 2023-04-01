package io.gardenerframework.camellia.authentication.server.main.mfa.event.listener;

import io.gardenerframework.camellia.authentication.infra.challenge.core.exception.ChallengeInCooldownException;
import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEngineComponent;
import io.gardenerframework.camellia.authentication.server.main.event.listener.AuthenticationEventListenerSkeleton;
import io.gardenerframework.camellia.authentication.server.main.event.listener.annotation.CareForAuthenticationServerEnginePreservedPrincipal;
import io.gardenerframework.camellia.authentication.server.main.event.schema.AuthenticationSuccessEvent;
import io.gardenerframework.camellia.authentication.server.main.event.schema.UserAuthenticatedEvent;
import io.gardenerframework.camellia.authentication.server.main.event.support.AuthenticationEventBuilder;
import io.gardenerframework.camellia.authentication.server.main.exception.NestedAuthenticationException;
import io.gardenerframework.camellia.authentication.server.main.mfa.advisor.AuthenticationServerMfaAuthenticatorAdvisor;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.AuthenticationServerMfaAuthenticationChallengeResponseService;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.AuthenticationServerMfaAuthenticationChallenge;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.AuthenticationServerMfaAuthenticationChallengeContext;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.AuthenticationServerMfaAuthenticationChallengeRequest;
import io.gardenerframework.camellia.authentication.server.main.mfa.exception.client.MfaAuthenticationInCooldownException;
import io.gardenerframework.camellia.authentication.server.main.mfa.exception.client.MfaAuthenticationRequiredException;
import io.gardenerframework.camellia.authentication.server.main.mfa.schema.principal.MfaAuthenticationPrincipal;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Objects;

/**
 * 在监听事件是
 *
 * @author ZhangHan
 * @date 2022/4/28 0:31
 */
@Slf4j
@RequiredArgsConstructor
@AuthenticationServerEngineComponent
public class MfaAuthenticationListener implements
        AuthenticationEventListenerSkeleton,
        ApplicationEventPublisherAware,
        AuthenticationEventBuilder {
    /**
     * 获取mfa认证器的组件
     */
    @NonNull
    private final Collection<AuthenticationServerMfaAuthenticatorAdvisor> mfaAuthenticationAdvisors;
    @NonNull
    private final AuthenticationServerMfaAuthenticationChallengeResponseService authenticationServerMfaAuthenticationChallengeResponseService;
    @NonNull
    private ApplicationEventPublisher eventPublisher;

    @Override
    @EventListener
    public void onUserAuthenticated(UserAuthenticatedEvent event) throws AuthenticationException {
        //没有advisor
        if (CollectionUtils.isEmpty(mfaAuthenticationAdvisors)) {
            return;
        }
        //用户认证结束后看看是否要发mfa请求
        //这里肯定是不关注保留登录名引发的用户登录行为的
        //这里还有一个逻辑
        //用户不完成mfa挑战一直无脑刷登录
        //此时mfa服务应当返回上一次的mfa挑战id直到ttl到达后再做决策
        try {
            String authenticator = null;
            for (AuthenticationServerMfaAuthenticatorAdvisor advisor : mfaAuthenticationAdvisors) {
                authenticator = advisor.getAuthenticator(
                        event.getRequest(),
                        event.getClient(),
                        event.getAuthenticationType(),
                        event.getUser(),
                        event.getContext()
                );
                if (StringUtils.hasText(authenticator)) {
                    //当前要求执行mfa认证
                    break;
                }
            }
            if (StringUtils.hasText(authenticator)) {
                //这部分找不到是服务端的问题，不单独开异常
                //执行mfa认证
                AuthenticationServerMfaAuthenticationChallenge mfaAuthenticationChallenge = authenticationServerMfaAuthenticationChallengeResponseService.sendChallenge(
                        event.getClient(),
                        authenticationServerMfaAuthenticationChallengeResponseService.getClass(),
                        new AuthenticationServerMfaAuthenticationChallengeRequest(authenticator, event.getPrincipal(), event.getUser(), event.getContext())
                );
                throw new MfaAuthenticationRequiredException(mfaAuthenticationChallenge.getTarget());
            }
        } catch (MfaAuthenticationRequiredException exception) {
            throw exception;
        } catch (ChallengeInCooldownException exception) {
            throw new MfaAuthenticationInCooldownException(exception.getTimeRemaining());
        } catch (Exception exception) {
            throw new NestedAuthenticationException(exception);
        }
    }

    @Override
    @EventListener
    @CareForAuthenticationServerEnginePreservedPrincipal
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        if (event.getPrincipal() instanceof MfaAuthenticationPrincipal) {
            //fix 需要从上下文取出挑战上下文(因为此时已经被关闭)
            AuthenticationServerMfaAuthenticationChallengeContext authenticationServerMfaAuthenticationChallengeContext = Objects.requireNonNull(
                    (AuthenticationServerMfaAuthenticationChallengeContext) event.getContext()
                            .get(AuthenticationServerMfaAuthenticationChallengeContext.class.getName()),
                    "no MfaAuthenticationChallengeContext load from context. review MfaAuthenticationUserService.load!"
            );
            AuthenticationSuccessEvent replay = buildAuthenticationEvent(
                    AuthenticationSuccessEvent.builder(),
                    event.getRequest(),
                    event.getAuthenticationType(),
                    authenticationServerMfaAuthenticationChallengeContext.getPrincipal(),
                    //客户端没有严格的id必须一样的逻辑，实际实现
                    //这里没有使用上下文的客户端，而是当前完成认证的，使得事件更贴近实际
                    event.getClient(),
                    event.getContext()
            ).user(event.getUser()).build();
            //重放一个假的登录成功事件从而解决mfa认证成功后一般监听器收不到登录成功事件的问题
            eventPublisher.publishEvent(replay);
        }
    }

    @Override
    public void setApplicationEventPublisher(@NonNull ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }
}
