package io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema;

import io.gardenerframework.camellia.authentication.common.data.serialization.SerializationVersionNumber;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.schema.DefaultSmsVerificationCodeChallengeContext;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.schema.SmsVerificationCodeChallengeContext;
import lombok.NoArgsConstructor;
import lombok.experimental.Delegate;

@NoArgsConstructor
public class SmsMfaAuthenticationChallengeContext implements MfaAuthenticationChallengeContext, SmsVerificationCodeChallengeContext {
    private static final long serialVersionUID = SerializationVersionNumber.version;
    @Delegate
    private final SmsVerificationCodeChallengeContext smsVerificationCodeChallengeContext = new DefaultSmsVerificationCodeChallengeContext();
    @Delegate
    private final MfaAuthenticationChallengeContext mfaAuthenticationChallengeContext = new DefaultMfaAuthenticationChallengeContext();
}
