package io.gardenerframework.camellia.authentication.infra.sms.challenge.event.schema;

import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

@SuperBuilder
public class SmsVerificationCodeSendingFailedEvent extends SmsVerificationCodeEvent {
    @NotNull
    private final Exception exception;
}
