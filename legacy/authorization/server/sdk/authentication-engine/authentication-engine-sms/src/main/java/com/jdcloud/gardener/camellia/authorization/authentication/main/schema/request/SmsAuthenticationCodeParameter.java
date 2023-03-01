package com.jdcloud.gardener.camellia.authorization.authentication.main.schema.request;

import lombok.Getter;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;

/**
 * @author ZhangHan
 * @date 2022/5/15 13:55
 */
@Getter
public class SmsAuthenticationCodeParameter extends AbstractAuthenticationRequestParameter {
    @NotBlank
    private final String mobilePhoneNumber;
    @NotBlank
    private final String code;

    public SmsAuthenticationCodeParameter(HttpServletRequest request) {
        super(request);
        this.mobilePhoneNumber = request.getParameter("mobilePhoneNumber");
        this.code = request.getParameter("code");
    }
}
