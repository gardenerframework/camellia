package io.gardenerframework.camellia.authentication.infra.sms.engine.test.utils;

import io.gardenerframework.camellia.authentication.infra.sms.core.event.schema.SmsAuthenticationAboutToSendEvent;
import io.gardenerframework.camellia.authentication.infra.sms.core.event.schema.SmsAuthenticationFailToSendEvent;
import io.gardenerframework.camellia.authentication.infra.sms.core.event.schema.SmsAuthenticationSentEvent;
import lombok.Getter;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author zhanghan30
 * @date 2023/2/15 15:56
 */
@Component
@Getter
public class SmsAuthenticationEventListener {
    public boolean aboutToSend = false;
    public boolean sent = false;
    public boolean failToSend = false;

    @EventListener
    public void onAboutToSend(
            SmsAuthenticationAboutToSendEvent event
    ) {
        this.aboutToSend = true;
    }

    @EventListener
    public void onSent(
            SmsAuthenticationSentEvent event
    ) {
        this.sent = true;
    }

    public void onFailToSend(
            SmsAuthenticationFailToSendEvent event
    ) {
        this.failToSend = true;
    }
}
