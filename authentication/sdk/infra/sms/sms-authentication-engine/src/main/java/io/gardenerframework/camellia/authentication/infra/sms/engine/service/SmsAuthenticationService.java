package io.gardenerframework.camellia.authentication.infra.sms.engine.service;

import io.gardenerframework.camellia.authentication.infra.sms.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.sms.core.SmsAuthenticationClient;
import io.gardenerframework.camellia.authentication.infra.sms.core.SmsAuthenticationCodeStore;
import io.gardenerframework.camellia.authentication.infra.sms.engine.exceptions.SmsAuthenticationInCooldownException;
import io.gardenerframework.camellia.authentication.infra.sms.engine.exceptions.SmsAuthenticationServiceException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.util.Objects;

/**
 * @author zhanghan30
 * @date 2023/2/14 17:53
 */
public class SmsAuthenticationService {
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

    /**
     * 发送验证码
     *
     * @param applicationId     应用程序id。用来分辨当前验证码是什么应用程序要求发出
     * @param mobilePhoneNumber 手机号
     * @param scenario          什么场景下发出
     * @param code              验证码
     * @param cooldown          冷却时间
     */
    public void sendCode(
            @NonNull String applicationId,
            @NonNull String mobilePhoneNumber,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String code,
            @NonNull Duration cooldown
    ) {
        try {
            //获取剩余时间
            Duration timeRemaining = smsAuthenticationCodeStore.getTimeRemaining(
                    applicationId,
                    mobilePhoneNumber,
                    scenario
            );
            if (timeRemaining != null) {
                //还在cd中
                throw new SmsAuthenticationInCooldownException(timeRemaining);
            }
            //注意，如果底层存储需要处理同一id多次存储的问题，并确保只有一次存储成功
            //存储验证码
            if (smsAuthenticationCodeStore.saveCodeIfAbsent(
                    applicationId,
                    mobilePhoneNumber,
                    scenario,
                    encodeCode(code),
                    cooldown
            )) {
                //发送验证码
                smsAuthenticationClient.sendCode(
                        applicationId,
                        mobilePhoneNumber,
                        scenario,
                        code
                );
            }
        } catch (Exception e) {
            //转为认证服务异常
            throw new SmsAuthenticationServiceException(e);
        }
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
    ) {
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


    @Nullable
    public Duration getTimeRemaining(
            @NonNull String applicationId,
            @NonNull String mobilePhoneNumber,
            @NonNull Class<? extends Scenario> scenario
    ) {
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
}
