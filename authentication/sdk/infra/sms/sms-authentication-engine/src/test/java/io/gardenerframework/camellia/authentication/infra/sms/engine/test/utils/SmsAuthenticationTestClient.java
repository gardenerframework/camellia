package io.gardenerframework.camellia.authentication.infra.sms.engine.test.utils;

import io.gardenerframework.camellia.authentication.infra.sms.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.sms.core.SmsAuthenticationClient;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhanghan30
 * @date 2023/2/15 15:32
 */
@Component
public class SmsAuthenticationTestClient implements SmsAuthenticationClient {
    @Getter
    private final AtomicInteger counter = new AtomicInteger();

    public void resetCounter() {
        counter.set(0);
    }

    @Override
    public void sendCode(String applicationId, String mobilePhoneNumber, Class<? extends Scenario> scenario, String code) throws Exception {
        counter.incrementAndGet();
    }
}
