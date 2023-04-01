package io.gardenerframework.camellia.authentication.infra.sms.challenge.schema;

import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class SmsVerificationCodeChallengeRequest implements ChallengeRequest {
    @NonNull
    @Builder.Default
    private String mobilePhoneNumber = "";
}
