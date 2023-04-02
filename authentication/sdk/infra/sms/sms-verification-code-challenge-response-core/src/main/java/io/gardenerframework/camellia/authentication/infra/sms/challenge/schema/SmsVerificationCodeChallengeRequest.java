package io.gardenerframework.camellia.authentication.infra.sms.challenge.schema;

import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class SmsVerificationCodeChallengeRequest implements ChallengeRequest {
    /**
     * 发起请求的手机号
     */
    @NonNull
    @Builder.Default
    @NotBlank
    private String mobilePhoneNumber = "";

    /**
     * 是否将手机号作为挑战id直接使用
     * <p>
     * 这种可用于手机登录场景
     */
    @Builder.Default
    private boolean mobilePhoneNumberAsChallengeId = false;
}
