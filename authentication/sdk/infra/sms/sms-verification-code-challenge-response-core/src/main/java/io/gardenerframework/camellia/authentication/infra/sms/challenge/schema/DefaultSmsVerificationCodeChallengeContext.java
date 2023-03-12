package io.gardenerframework.camellia.authentication.infra.sms.challenge.schema;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class DefaultSmsVerificationCodeChallengeContext implements SmsVerificationCodeChallengeContext {
    @NonNull
    private String code;
}
