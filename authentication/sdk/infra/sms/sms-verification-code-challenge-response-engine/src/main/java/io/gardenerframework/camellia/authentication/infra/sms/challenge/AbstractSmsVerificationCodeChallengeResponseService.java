package io.gardenerframework.camellia.authentication.infra.sms.challenge;

import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeContextStore;
import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeCooldownManager;
import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeStore;
import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.challenge.core.annotation.ChallengeAuthenticator;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.AbstractChallengeResponseService;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.client.SmsVerificationCodeClient;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.event.schema.SmsVerificationCodeAboutToSendEvent;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.event.schema.SmsVerificationCodeSendingFailedEvent;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.event.schema.SmsVerificationCodeSentEvent;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.schema.SmsVerificationCodeChallengeContext;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.schema.SmsVerificationCodeChallengeRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.lang.Nullable;

import java.security.SecureRandom;
import java.util.Map;

@ChallengeAuthenticator("sms")
public abstract class AbstractSmsVerificationCodeChallengeResponseService<R extends SmsVerificationCodeChallengeRequest, C extends Challenge, X extends SmsVerificationCodeChallengeContext>
        extends AbstractChallengeResponseService<R, C, X> implements ApplicationEventPublisherAware {
    @NonNull
    @Getter(AccessLevel.PROTECTED)
    private final SmsVerificationCodeClient smsVerificationCodeClient;

    private ApplicationEventPublisher eventPublisher;

    protected AbstractSmsVerificationCodeChallengeResponseService(@NonNull ChallengeStore<C> challengeStore, @NonNull ChallengeCooldownManager challengeCooldownManager, @NonNull ChallengeContextStore<X> challengeContextStore, @NonNull SmsVerificationCodeClient smsVerificationCodeClient) {
        super(challengeStore, challengeCooldownManager, challengeContextStore);
        this.smsVerificationCodeClient = smsVerificationCodeClient;
    }


    @Override
    protected boolean replayChallenge(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull R request) {
        //??????????????????cd??????????????????
        return false;
    }

    @Override
    protected @NonNull String getRequestSignature(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull R request) {
        //??????????????????????????????????????????????????????????????????
        return request.getMobilePhoneNumber();
    }

    @Override
    protected boolean hasCooldown(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull R request) {
        //??????????????????cd
        return true;
    }

    @Override
    protected int getCooldownTime(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull R request) {
        //?????????????????????
        return 60;
    }

    @Override
    protected @NonNull String getCooldownTimerId(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull R request) {
        //????????????cd
        return request.getMobilePhoneNumber();
    }

    /**
     * ???????????????
     *
     * @param client   ?????????
     * @param scenario ??????
     * @param request  ????????????
     * @param payload  ??????
     * @return ?????????
     */
    protected String generateCode(@Nullable RequestingClient client,
                                  @NonNull Class<? extends Scenario> scenario,
                                  @NonNull R request,
                                  @NonNull Map<String, Object> payload
    ) {
        return String.format("%06d", new SecureRandom().nextInt(999999 + 1));
    }

    /**
     * ?????????????????????
     *
     * @param client   ?????????
     * @param scenario ??????
     * @param request  ??????
     * @param payload  ??????
     * @return ??????
     */
    abstract protected C createSmsVerificationChallenge(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull R request, @NonNull Map<String, Object> payload);

    @Override
    final protected C sendChallengeInternally(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull R request, @NonNull Map<String, Object> payload) throws Exception {
        //???????????????
        String mobilePhoneNumber = request.getMobilePhoneNumber();
        //???????????????
        String code = generateCode(client, scenario, request, payload);
        //??????????????????????????????
        payload.put(AbstractSmsVerificationCodeChallengeResponseService.class.getName(), code);
        this.eventPublisher.publishEvent(
                SmsVerificationCodeAboutToSendEvent.builder()
                        .client(client)
                        .scenario(scenario)
                        .request(request)
                        .payload(payload)
                        .build()
        );
        try {
            //??????????????????
            smsVerificationCodeClient.sendVerificationCode(client, mobilePhoneNumber, scenario, code);
        } catch (Exception e) {
            eventPublisher.publishEvent(
                    SmsVerificationCodeSendingFailedEvent.builder()
                            .client(client)
                            .scenario(scenario)
                            .request(request)
                            .payload(payload)
                            .exception(e)
                            .build()
            );
            throw e;
        }
        this.eventPublisher.publishEvent(
                SmsVerificationCodeSentEvent.builder()
                        .client(client)
                        .scenario(scenario)
                        .request(request)
                        .payload(payload)
                        .build()
        );
        //????????????
        return createSmsVerificationChallenge(client, scenario, request, payload);
    }

    /**
     * ??????sms????????????????????????
     *
     * @param client    ?????????
     * @param scenario  ??????
     * @param request   ??????
     * @param challenge ???????????????
     * @param payload   ??????
     * @return ?????????
     */
    @NonNull
    abstract protected X createSmsVerificationChallengeContext(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull R request, @NonNull C challenge, @NonNull Map<String, Object> payload);

    @Override
    final protected X createContext(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull R request, @NonNull C challenge, @NonNull Map<String, Object> payload) {
        X context = createSmsVerificationChallengeContext(client, scenario, request, challenge, payload);
        //???????????????
        context.setCode(String.valueOf(payload.get(AbstractSmsVerificationCodeChallengeResponseService.class.getName())));
        return context;
    }

    @Override
    protected boolean verifyChallengeInternally(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull String challengeId, @NonNull X context, @NonNull String response) throws Exception {
        //?????????????????????????????????????????????
        return response.equals(context.getCode());
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }
}
