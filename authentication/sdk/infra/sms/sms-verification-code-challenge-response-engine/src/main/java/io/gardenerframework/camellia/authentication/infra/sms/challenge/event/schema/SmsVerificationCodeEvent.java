package io.gardenerframework.camellia.authentication.infra.sms.challenge.event.schema;

import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.schema.SmsVerificationCodeChallengeRequest;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.util.Map;

@SuperBuilder
@Getter
public abstract class SmsVerificationCodeEvent {
    @Nullable
    private final RequestingClient client;
    @NotNull
    private final Class<? extends Scenario> scenario;
    @NotNull
    private final SmsVerificationCodeChallengeRequest request;
    @NotNull
    private final Map<String, Object> payload;
}
