package com.jdcloud.gardener.camellia.uac.account.defaults.schema.request;

import com.jdcloud.gardener.camellia.uac.account.schema.request.UpdateAccountParameterTemplate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @author ZhangHan
 * @date 2022/11/8 11:25
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class DefaultUpdateAccountParameter extends UpdateAccountParameterTemplate {
    public DefaultUpdateAccountParameter(String avatar, String nickname, @NotBlank String surname, @NotBlank String givenName, Date dateOfBirth, Integer gender, String ethnicGroup, String officeTelephoneNumber, String fax, String challengeId, String response, String captchaToken) {
        super(avatar, nickname, surname, givenName, dateOfBirth, gender, ethnicGroup, officeTelephoneNumber, fax, challengeId, response, captchaToken);
    }
}
