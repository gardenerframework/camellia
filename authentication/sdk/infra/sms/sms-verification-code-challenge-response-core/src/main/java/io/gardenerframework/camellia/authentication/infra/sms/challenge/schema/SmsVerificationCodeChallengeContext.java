package io.gardenerframework.camellia.authentication.infra.sms.challenge.schema;

import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeContext;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class SmsVerificationCodeChallengeContext implements ChallengeContext {
    @NonNull
    private String code;
}
