package com.jdcloud.gardener.camellia.uac.account.defaults.schema.request;

import com.jdcloud.gardener.camellia.uac.account.schema.request.AuthenticateAccountParameterTemplate;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;

/**
 * @author ZhangHan
 * @date 2022/11/8 11:24
 */
@NoArgsConstructor
@SuperBuilder
public class DefaultAuthenticateAccountParameter extends AuthenticateAccountParameterTemplate {
    public DefaultAuthenticateAccountParameter(String username, String mobilePhoneNumber, String email, @NotBlank String password, String captchaToken) {
        super(username, mobilePhoneNumber, email, password, captchaToken);
    }
}
