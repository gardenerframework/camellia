package io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema;

import io.gardenerframework.camellia.authentication.common.data.serialization.SerializationVersionNumber;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.schema.SmsVerificationCodeChallengeContext;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public class SmsMfaChallengeContext extends MfaAuthenticationChallengeContext implements SmsVerificationCodeChallengeContext {
    private static final long serialVersionUID = SerializationVersionNumber.version;
    private String code;
}
