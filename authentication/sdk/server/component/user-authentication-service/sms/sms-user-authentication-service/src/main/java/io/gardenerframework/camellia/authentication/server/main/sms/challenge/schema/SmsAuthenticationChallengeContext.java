package io.gardenerframework.camellia.authentication.server.main.sms.challenge.schema;

import io.gardenerframework.camellia.authentication.common.data.serialization.SerializationVersionNumber;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.schema.SmsVerificationCodeChallengeContext;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class SmsAuthenticationChallengeContext implements SmsVerificationCodeChallengeContext {
    private static final long serialVersionUID = SerializationVersionNumber.version;
    @NonNull
    private String code;
}
