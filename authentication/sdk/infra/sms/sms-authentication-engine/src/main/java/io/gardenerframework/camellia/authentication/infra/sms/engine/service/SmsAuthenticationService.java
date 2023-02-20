package io.gardenerframework.camellia.authentication.infra.sms.engine.service;

import io.gardenerframework.camellia.authentication.infra.common.Scenario;
import io.gardenerframework.camellia.authentication.infra.sms.core.SmsAuthenticationClient;
import io.gardenerframework.camellia.authentication.infra.sms.core.SmsAuthenticationCodeStore;
import io.gardenerframework.camellia.authentication.infra.sms.core.event.schema.SmsAuthenticationAboutToSendEvent;
import io.gardenerframework.camellia.authentication.infra.sms.core.event.schema.SmsAuthenticationFailToSendEvent;
import io.gardenerframework.camellia.authentication.infra.sms.core.event.schema.SmsAuthenticationSentEvent;
import io.gardenerframework.camellia.authentication.infra.sms.engine.exceptions.SmsAuthenticationInCooldownException;
import io.gardenerframework.camellia.authentication.infra.sms.engine.exceptions.SmsAuthenticationServiceException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.util.Objects;

/**
 * @author zhanghan30
 * @date 2023/2/14 17:53
 */
public class SmsAuthenticationService implements ApplicationEventPublisherAware {
    /**
     * 存储
     */
    @Setter(value = AccessLevel.PRIVATE, onMethod = @__({@Autowired}))
    @Getter(AccessLevel.PROTECTED)
    private SmsAuthenticationCodeStore smsAuthenticationCodeStore;
    /**
     * 客户端
     */
    @Setter(value = AccessLevel.PRIVATE, onMethod = @__({@Autowired}))
    @Getter(AccessLevel.PROTECTED)
    private SmsAuthenticationClient smsAuthenticationClient;

    private ApplicationEventPublisher eventPublisher;

    /**
     * 发送验证码
     *
     * @param applicationId     应用程序id。用来分辨当前验证码是什么应用程序要求发出
     * @param mobilePhoneNumber 手机号
     * @param scenario          什么场景下发出
     * @param code              验证码
     * @param cooldown          冷却时间
     * @throws SmsAuthenticationInCooldownException 当前发送还不可用
     * @throws SmsAuthenticationServiceException    发送服务出现问题
     */
    public void sendCode(
            @NonNull String applicationId,
            @NonNull String mobilePhoneNumber,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String code,
            @NonNull Duration cooldown
    ) throws SmsAuthenticationInCooldownException, SmsAuthenticationServiceException {
        //获取剩余时间
        Duration timeRemaining = getTimeRemaining(
                applicationId,
                mobilePhoneNumber,
                scenario
        );
        if (timeRemaining != null) {
            //还在cd中
            throw new SmsAuthenticationInCooldownException(timeRemaining);
        }
        boolean codeSaved = false;
        try {
            codeSaved = smsAuthenticationCodeStore.saveCodeIfAbsent(
                    applicationId,
                    mobilePhoneNumber,
                    scenario,
                    encodeCode(code),
                    cooldown
            );
        } catch (Exception e) {
            throw new SmsAuthenticationServiceException(e);
        }
        //没有保存成功则不发送
        if (!codeSaved) {
            return;
        }
        //成功存储了认证码
        //发送应用事件
        eventPublisher.publishEvent(
                new SmsAuthenticationAboutToSendEvent(
                        applicationId,
                        mobilePhoneNumber,
                        scenario
                )
        );
        try {
            //发送验证码
            smsAuthenticationClient.sendCode(
                    applicationId,
                    mobilePhoneNumber,
                    scenario,
                    code
            );
        } catch (Exception e) {
            //发送失败
            try {
                //尝试删除验证码
                smsAuthenticationCodeStore.removeCode(
                        applicationId,
                        mobilePhoneNumber,
                        scenario
                );
            } catch (Exception notRemoved) {
                //删除也报错了
                throw new SmsAuthenticationServiceException(notRemoved);
            }
            //发送失败事件
            eventPublisher.publishEvent(
                    new SmsAuthenticationFailToSendEvent(
                            applicationId,
                            mobilePhoneNumber,
                            scenario,
                            e
                    )
            );
            //转发送失败的错误
            throw new SmsAuthenticationServiceException(e);
        }
        //发布成功发送事件
        eventPublisher.publishEvent(
                new SmsAuthenticationSentEvent(
                        applicationId,
                        mobilePhoneNumber,
                        scenario
                )
        );
    }

    /**
     * 校验验证码是否正确
     *
     * @param applicationId     应用程序id。用来分辨当前验证码是什么应用程序要求发出
     * @param mobilePhoneNumber 手机号
     * @param scenario          什么场景下发出
     * @param code              验证码
     * @return 是否验证通过
     */
    public boolean verifyCode(
            @NonNull String applicationId,
            @NonNull String mobilePhoneNumber,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String code
    ) throws SmsAuthenticationServiceException {
        try {
            String codeSaved = smsAuthenticationCodeStore.getCode(
                    applicationId,
                    mobilePhoneNumber,
                    scenario
            );
            return Objects.equals(codeSaved, encodeCode(code));
        } catch (Exception e) {
            //转为认证服务异常
            throw new SmsAuthenticationServiceException(e);
        }
    }


    /**
     * 获取验证码的剩余发送时间
     *
     * @param applicationId     应用服务id
     * @param mobilePhoneNumber 手机号
     * @param scenario          场景
     * @return 剩余时间
     */
    @Nullable
    public Duration getTimeRemaining(
            @NonNull String applicationId,
            @NonNull String mobilePhoneNumber,
            @NonNull Class<? extends Scenario> scenario
    ) throws SmsAuthenticationServiceException {
        try {
            return smsAuthenticationCodeStore.getTimeRemaining(
                    applicationId,
                    mobilePhoneNumber,
                    scenario
            );
        } catch (Exception e) {
            //转为认证服务异常
            throw new SmsAuthenticationServiceException(e);
        }
    }

    /**
     * 如果现场需要进行验证码的编码，则覆盖本方法
     *
     * @param code 原来的验证码
     * @return 编码后的
     */
    @NonNull
    protected String encodeCode(@NonNull String code) {
        return code;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }
}
