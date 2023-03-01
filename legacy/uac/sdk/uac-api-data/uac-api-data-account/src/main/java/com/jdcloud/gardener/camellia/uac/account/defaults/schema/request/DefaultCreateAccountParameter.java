package com.jdcloud.gardener.camellia.uac.account.defaults.schema.request;

import com.jdcloud.gardener.camellia.uac.account.schema.request.CreateAccountParameterTemplate;
import com.jdcloud.gardener.camellia.uac.account.schema.request.EmailProperty;
import com.jdcloud.gardener.camellia.uac.account.schema.request.MobilePhoneNumberProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @author ZhangHan
 * @date 2022/11/8 11:28
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class DefaultCreateAccountParameter extends CreateAccountParameterTemplate {
    public DefaultCreateAccountParameter(@NotBlank String username, String avatar, String nickname, @Nullable String password, @NotBlank String surname, @NotBlank String givenName, Date dateOfBirth, Integer gender, String ethnicGroup, @Valid EmailProperty emailElement, @Valid MobilePhoneNumberProperty mobilePhoneNumberElement, String officeTelephoneNumber, String fax, Date accountExpiryDate, String weChatOpenIdToken, String enterpriseWeChatOpenIdToken, String alipayOpenIdToken, String dingTalkOpenIdToken, String larkOpenIdToken, String captchaToken, String challengeId, String response) {
        super(username, avatar, nickname, password, surname, givenName, dateOfBirth, gender, ethnicGroup, emailElement, mobilePhoneNumberElement, officeTelephoneNumber, fax, accountExpiryDate, weChatOpenIdToken, enterpriseWeChatOpenIdToken, alipayOpenIdToken, dingTalkOpenIdToken, larkOpenIdToken, captchaToken, challengeId, response);
    }
}
