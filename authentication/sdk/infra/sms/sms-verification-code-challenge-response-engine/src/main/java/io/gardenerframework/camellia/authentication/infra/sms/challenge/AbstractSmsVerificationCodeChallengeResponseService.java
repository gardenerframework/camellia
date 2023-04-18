package io.gardenerframework.camellia.authentication.infra.sms.challenge;

import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeContextStore;
import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeCooldownManager;
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
import io.gardenerframework.fragrans.log.GenericLoggerStaticAccessor;
import io.gardenerframework.fragrans.log.common.schema.verb.Change;
import io.gardenerframework.fragrans.log.schema.content.GenericBasicLogContent;
import io.gardenerframework.fragrans.log.schema.details.Detail;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.lang.Nullable;

import java.security.SecureRandom;
import java.util.Map;

@ChallengeAuthenticator("sms")
@Slf4j
public abstract class AbstractSmsVerificationCodeChallengeResponseService<R extends SmsVerificationCodeChallengeRequest, C extends Challenge, X extends SmsVerificationCodeChallengeContext>
        extends AbstractChallengeResponseService<R, C, X> implements ApplicationEventPublisherAware {
    @NonNull
    @Getter(AccessLevel.PROTECTED)
    private final SmsVerificationCodeClient smsVerificationCodeClient;

    private ApplicationEventPublisher eventPublisher;

    protected AbstractSmsVerificationCodeChallengeResponseService(@NonNull ChallengeCooldownManager challengeCooldownManager, @NonNull ChallengeContextStore<X> challengeContextStore, @NonNull SmsVerificationCodeClient smsVerificationCodeClient) {
        super(challengeCooldownManager, challengeContextStore);
        this.smsVerificationCodeClient = smsVerificationCodeClient;
    }

    @Override
    protected boolean hasCooldown(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull R request) {
        //原则说一定有cd
        return true;
    }

    @Override
    protected int getCooldownTime(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull R request) {
        //一般说是一分钟
        return 60;
    }

    @Override
    protected @NonNull String getCooldownTimerId(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull R request) {
        //按手机号cd
        return request.getMobilePhoneNumber();
    }

    /**
     * 生产验证码
     *
     * @param client   客户端
     * @param scenario 场景
     * @param request  挑战请求
     * @param payload  载荷
     * @return 验证码
     */
    protected String generateCode(@Nullable RequestingClient client,
                                  @NonNull Class<? extends Scenario> scenario,
                                  @NonNull R request,
                                  @NonNull Map<String, Object> payload
    ) {
        return String.format("%06d", new SecureRandom().nextInt(999999 + 1));
    }

    /**
     * 生成最后的挑战
     *
     * @param client   客户端
     * @param scenario 场景
     * @param request  请求
     * @param payload  载荷
     * @return 挑战
     */
    abstract protected C createSmsVerificationChallenge(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull R request, @NonNull Map<String, Object> payload);

    @Override
    final protected C sendChallengeInternally(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull R request, @NonNull Map<String, Object> payload) throws Exception {
        //获取手机号
        String mobilePhoneNumber = request.getMobilePhoneNumber();
        //生成验证码
        String code = generateCode(client, scenario, request, payload);
        //在上下文中保存验证码
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
            //调客户端发送
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
        //生成挑战
        C challenge = createSmsVerificationChallenge(client, scenario, request, payload);
        if (request.isMobilePhoneNumberAsChallengeId()) {
            String challengeId = challenge.getId();
            //记录一下因为这个改了
            GenericLoggerStaticAccessor.basicLogger().debug(
                    log,
                    GenericBasicLogContent.builder()
                            .what(challenge.getClass())
                            .how(new Change())
                            .detail(new Detail() {
                                private final String fromChallengeId = challengeId;
                                private final String toChallengeId = request.getMobilePhoneNumber();
                            }).build(),
                    null
            );
            challenge.setId(request.getMobilePhoneNumber());
        }
        return challenge;
    }

    /**
     * 生成sms验证码挑战上下文
     *
     * @param client    客户端
     * @param scenario  场景
     * @param request   请求
     * @param challenge 发送的挑战
     * @param payload   载荷
     * @return 上下文
     */
    @NonNull
    abstract protected X createSmsVerificationChallengeContext(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull R request, @NonNull C challenge, @NonNull Map<String, Object> payload);

    @Override
    final protected X createContext(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull R request, @NonNull C challenge, @NonNull Map<String, Object> payload) {
        X context = createSmsVerificationChallengeContext(client, scenario, request, challenge, payload);
        //设置验证码
        context.setCode(String.valueOf(payload.get(AbstractSmsVerificationCodeChallengeResponseService.class.getName())));
        return context;
    }

    @Override
    protected boolean verifyChallengeInternally(@Nullable RequestingClient client, @NonNull Class<? extends Scenario> scenario, @NonNull String challengeId, @NonNull X context, @NonNull String response) throws Exception {
        //输入的响应和上下文中保存的一样
        return response.equals(context.getCode());
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }
}
