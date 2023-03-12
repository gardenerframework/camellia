package io.gardenerframework.camellia.authentication.infra.sms.challenge.event.schema;

import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class SmsVerificationCodeSendingFailedEvent extends SmsVerificationCodeEvent {
    @NonNull
    private final Exception exception;
}
