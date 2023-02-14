package io.gardenerframework.camellia.authentication.infra.sms.core;

/**
 * @author zhanghan30
 * @date 2023/2/14 18:19
 */
public interface SmsAuthenticationClient {
    /**
     * 基于应用组，向指定的手机号发送短信
     *
     * @param applicationId     应用组
     * @param mobilePhoneNumber 发给哪个手机
     * @param code              验证码
     * @param scenario          当前发送使用的场景
     * @throws Exception 透传产生的问题
     */
    void sendCode(
            String applicationId,
            String mobilePhoneNumber,
            Class<? extends Scenario> scenario,
            String code
    ) throws Exception;
}
