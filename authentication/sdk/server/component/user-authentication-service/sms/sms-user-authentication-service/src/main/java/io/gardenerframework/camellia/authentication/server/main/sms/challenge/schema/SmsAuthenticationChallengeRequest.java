package io.gardenerframework.camellia.authentication.server.main.sms.challenge.schema;

import io.gardenerframework.camellia.authentication.infra.sms.challenge.schema.SmsVerificationCodeChallengeRequest;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class SmsAuthenticationChallengeRequest implements SmsVerificationCodeChallengeRequest {

    @NonNull
    private String mobilePhoneNumber;
}
