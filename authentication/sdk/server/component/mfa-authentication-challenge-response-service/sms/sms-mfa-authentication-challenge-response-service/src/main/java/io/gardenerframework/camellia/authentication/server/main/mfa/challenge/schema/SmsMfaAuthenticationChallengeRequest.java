package io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema;

import io.gardenerframework.camellia.authentication.infra.sms.challenge.schema.SmsVerificationCodeChallengeRequest;
import lombok.*;
import lombok.experimental.Delegate;


@NoArgsConstructor
@Getter
@Setter
public class SmsMfaAuthenticationChallengeRequest implements MfaAuthenticationChallengeRequest, SmsVerificationCodeChallengeRequest {
    @NonNull
    private String mobilePhoneNumber;
    @Delegate
    @Getter(AccessLevel.NONE)
    private final MfaAuthenticationChallengeRequest mfaAuthenticationChallengeRequest = new DefaultMfaAuthenticationChallengeRequest();
}
