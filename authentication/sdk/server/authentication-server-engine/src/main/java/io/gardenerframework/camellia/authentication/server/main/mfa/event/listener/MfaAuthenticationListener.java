package io.gardenerframework.camellia.authentication.server.main.mfa.event.listener;

import com.jdcloud.gardener.camellia.authorization.authentication.main.event.listener.annotation.CareForAuthorizationEnginePreservedPrincipal;
import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.NestedAuthenticationException;
import com.jdcloud.gardener.camellia.authorization.authentication.mfa.MfaAuthenticationChallengeResponseService;
import com.jdcloud.gardener.camellia.authorization.authentication.mfa.exception.client.MfaAuthenticationRequiredException;
import com.jdcloud.gardener.camellia.authorization.authentication.mfa.schema.MfaAuthenticationChallengeContext;
import com.jdcloud.gardener.camellia.authorization.authentication.mfa.schema.MfaAuthenticationChallengeRequest;
import com.jdcloud.gardener.camellia.authorization.challenge.exception.client.ChallengeException;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.Challenge;
import io.gardenerframework.camellia.authentication.server.main.event.listener.AuthenticationEventListenerSkeleton;
import io.gardenerframework.camellia.authentication.server.main.event.schema.AuthenticationSuccessEvent;
import io.gardenerframework.camellia.authentication.server.main.event.schema.UserAuthenticatedEvent;
import io.gardenerframework.camellia.authentication.server.main.schema.principal.MfaChallengeIdPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;

/**
 * 在监听事件是
 *
 * @author ZhangHan
 * @date 2022/4/28 0:31
 */
@Slf4j
@RequiredArgsConstructor
public class MfaAuthenticationListener implements AuthenticationEventListenerSkeleton, ApplicationEventPublisherAware {
    private final MfaAuthenticationChallengeResponseService mfaAuthenticationChallengeResponseService;
    private ApplicationEventPublisher eventPublisher;


    @Override
    @EventListener
    public void onUserAuthenticated(UserAuthenticatedEvent event) throws AuthenticationException {
        //用户认证结束后看看是否要发mfa请求
        //这里肯定是不关注保留登录名引发的用户登录行为的
        //这里还有一个逻辑
        //用户不完成mfa挑战一直无脑刷登录
        //此时mfa服务应当返回上一次的mfa挑战id直到ttl到达后再做决策
        try {
            Challenge mfaAuthenticationChallenge = mfaAuthenticationChallengeResponseService.sendChallenge(
                    new MfaAuthenticationChallengeRequest(
                            event.getHeaders(),
                            event.getClientGroup(),
                            event.getClient(),
                            event.getUser(),
                            //added, 增加当前需要决策触发挑战的登录名
                            event.getPrincipal(),
                            event.getContext()
                    )
            );
            if (mfaAuthenticationChallenge == null) {
                return;
            }
            throw new MfaAuthenticationRequiredException(mfaAuthenticationChallenge);
        } catch (ChallengeException exception) {
            throw new NestedAuthenticationException(exception);
        }
    }

    @Override
    @EventListener
    @CareForAuthorizationEnginePreservedPrincipal
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        if (event.getPrincipal() instanceof MfaChallengeIdPrincipal) {
            MfaAuthenticationChallengeContext context = (MfaAuthenticationChallengeContext) event.getContext().get(MfaAuthenticationChallengeContext.class.getName());
            Assert.notNull(context, "MfaAuthenticationChallengeContext must not be null, check MfaChallengeAuthenticationUserService::load");
            AuthenticationSuccessEvent replay = new AuthenticationSuccessEvent(
                    event.getHeaders(),
                    event.getAuthenticationType(),
                    context.getPrincipal(),
                    event.getClientGroup(),
                    event.getClient(),
                    event.getContext(),
                    event.getUser()
            );
            //重放一个假的登录成功事件从而解决mfa认证成功后一般监听器收不到登录成功事件的问题
            eventPublisher.publishEvent(replay);
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }
}
