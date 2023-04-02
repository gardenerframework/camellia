package io.gardenerframework.camellia.authentication.infra.sms.challenge.test.beans;

import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.client.SmsVerificationCodeClient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * @author chris
 * <p>
 * date: 2023/4/2
 */
@NoArgsConstructor
@Component
public class TestSmsVerificationCodeClient implements SmsVerificationCodeClient {
    @Getter
    private String code;

    @Override
    public void sendVerificationCode(@Nullable RequestingClient client, @NonNull String mobilePhoneNumber, @NonNull Class<? extends Scenario> scenario, @NonNull String code) throws Exception {
        this.code = code;
    }
}
