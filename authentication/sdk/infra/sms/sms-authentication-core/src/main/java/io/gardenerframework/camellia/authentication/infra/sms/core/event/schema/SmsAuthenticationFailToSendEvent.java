package io.gardenerframework.camellia.authentication.infra.sms.core.event.schema;

import io.gardenerframework.camellia.authentication.infra.common.Scenario;
import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * @author zhanghan30
 * @date 2023/2/15 13:56
 */
@AllArgsConstructor
public class SmsAuthenticationFailToSendEvent {
    /**
     * 什么应用
     */
    @NonNull
    private final String applicationId;
    /**
     * 手机号
     */
    @NonNull
    private final String mobilePhoneNumber;
    /**
     * 场景
     */
    @NonNull
    private final Class<? extends Scenario> scenario;
    /**
     * 失败原因
     */
    @NonNull
    private final Exception exception;
}
