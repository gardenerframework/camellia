package io.gardenerframework.camellia.authentication.infra.sms.challenge.client;

import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import lombok.NonNull;
import org.springframework.lang.Nullable;

@FunctionalInterface
public interface SmsVerificationCodeClient {
    /**
     * 发送验证码
     *
     * @param client            客户端
     * @param mobilePhoneNumber 手机号
     * @param scenario          场景
     * @param code              验证码
     * @throws Exception 发送异常
     */
    void sendVerificationCode(
            @Nullable RequestingClient client,
            @NonNull String mobilePhoneNumber,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull String code
    ) throws Exception;
}
