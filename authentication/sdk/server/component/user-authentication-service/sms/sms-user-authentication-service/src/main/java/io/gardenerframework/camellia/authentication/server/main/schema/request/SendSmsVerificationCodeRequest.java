package io.gardenerframework.camellia.authentication.server.main.schema.request;

import io.gardenerframework.fragrans.data.trait.mankind.MankindTraits;
import io.gardenerframework.fragrans.data.trait.security.SecurityTraits;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class SendSmsVerificationCodeRequest implements
        MankindTraits.ContactTraits.MobilePhoneNumber,
        SecurityTraits.TuringTraits.CaptchaToken {
    @NotBlank
    private String mobilePhoneNumber;
    @NotBlank
    private String captchaToken;
}
