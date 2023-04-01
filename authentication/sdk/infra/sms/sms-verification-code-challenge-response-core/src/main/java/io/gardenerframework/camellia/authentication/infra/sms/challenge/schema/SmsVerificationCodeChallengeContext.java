package io.gardenerframework.camellia.authentication.infra.sms.challenge.schema;

import io.gardenerframework.camellia.authentication.common.data.serialization.SerializationVersionNumber;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeContext;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class SmsVerificationCodeChallengeContext implements ChallengeContext {
    private static final long serialVersionUID = SerializationVersionNumber.version;
    @NonNull
    @Builder.Default
    private String code = "";
}
