package io.gardenerframework.camellia.authentication.server.main.mfa.event.listener;

import io.gardenerframework.camellia.authentication.infra.challenge.core.exception.ChallengeInCooldownException;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEngineComponent;
import io.gardenerframework.camellia.authentication.server.main.event.listener.AuthenticationEventListenerSkeleton;
import io.gardenerframework.camellia.authentication.server.main.event.listener.annotation.CareForAuthenticationServerEnginePreservedPrincipal;
import io.gardenerframework.camellia.authentication.server.main.event.schema.AuthenticationSuccessEvent;
import io.gardenerframework.camellia.authentication.server.main.event.schema.UserAuthenticatedEvent;
import io.gardenerframework.camellia.authentication.server.main.event.support.AuthenticationEventBuilder;
import io.gardenerframework.camellia.authentication.server.main.exception.NestedAuthenticationException;
import io.gardenerframework.camellia.authentication.server.main.mfa.advisor.MfaAuthenticatorAdvisor;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.MfaAuthenticationChallengeResponseService;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.MfaAuthenticationScenario;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.MfaAuthenticationChallengeContext;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.MfaAuthenticationChallengeRequest;
import io.gardenerframework.camellia.authentication.server.main.mfa.exception.client.MfaAuthenticationInCooldownException;
import io.gardenerframework.camellia.authentication.server.main.mfa.exception.client.MfaAuthenticationRequiredException;
import io.gardenerframework.camellia.authentication.server.main.mfa.schema.principal.MfaAuthenticationPrincipal;
import io.gardenerframework.camellia.authentication.server.main.mfa.utils.MfaAuthenticationChallengeResponseServiceRegistry;
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
 * ??????????????????
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
     * ??????mfa??????????????????
     */
    @NonNull
    private final Collection<MfaAuthenticatorAdvisor> mfaAuthenticationAdvisors;
    @NonNull
    private final MfaAuthenticationChallengeResponseServiceRegistry registry;
    @NonNull
    private ApplicationEventPublisher eventPublisher;

    @Override
    @EventListener
    public void onUserAuthenticated(UserAuthenticatedEvent event) throws AuthenticationException {
        //fix - ?????????????????????????????????????????????????????????mfa
        if (CollectionUtils.isEmpty(registry.getAuthenticatorNames())) {
            return;
        }
        //???????????????????????????????????????mfa??????
        //?????????????????????????????????????????????????????????????????????
        //????????????????????????
        //???????????????mfa???????????????????????????
        //??????mfa??????????????????????????????mfa??????id??????ttl?????????????????????
        try {
            String authenticator = null;
            for (MfaAuthenticatorAdvisor advisor : mfaAuthenticationAdvisors) {
                authenticator = advisor.getAuthenticator(
                        event.getRequest(),
                        event.getClient(),
                        event.getAuthenticationType(),
                        event.getUser(),
                        event.getContext()
                );
                if (StringUtils.hasText(authenticator)) {
                    //??????????????????mfa??????
                    break;
                }
            }
            if (StringUtils.hasText(authenticator)) {
                //????????????????????????????????????????????????????????????
                MfaAuthenticationChallengeResponseService<MfaAuthenticationChallengeRequest, MfaAuthenticationChallengeContext> service = Objects.requireNonNull(registry.getMfaAuthenticationChallengeResponseService(authenticator));
                //??????mfa??????
                Challenge mfaAuthenticationChallenge = service.sendChallenge(
                        event.getClient(),
                        MfaAuthenticationScenario.class,
                        event.getPrincipal(),
                        event.getUser(),
                        event.getContext()

                );
                throw new MfaAuthenticationRequiredException(mfaAuthenticationChallenge);
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
            //fix ???????????????????????????????????????(???????????????????????????)
            MfaAuthenticationChallengeContext mfaAuthenticationChallengeContext = Objects.requireNonNull(
                    (MfaAuthenticationChallengeContext) event.getContext()
                            .get(MfaAuthenticationChallengeContext.class.getName()),
                    "no MfaAuthenticationChallengeContext load from context. review MfaAuthenticationUserService.load!"
            );
            AuthenticationSuccessEvent replay = buildAuthenticationEvent(
                    AuthenticationSuccessEvent.builder(),
                    event.getRequest(),
                    event.getAuthenticationType(),
                    mfaAuthenticationChallengeContext.getPrincipal(),
                    //????????????????????????id????????????????????????????????????
                    //???????????????????????????????????????????????????????????????????????????????????????????????????
                    event.getClient(),
                    event.getContext()
            ).user(event.getUser()).build();
            //????????????????????????????????????????????????mfa??????????????????????????????????????????????????????????????????
            eventPublisher.publishEvent(replay);
        }
    }

    @Override
    public void setApplicationEventPublisher(@NonNull ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }
}
