package io.gardenerframework.camellia.authentication.server.main.schema.request;

import io.gardenerframework.fragrans.data.trait.generic.GenericTraits;
import io.gardenerframework.fragrans.data.trait.mankind.MankindTraits;
import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;

/**
 * 短信认证的
 *
 * @author ZhangHan
 * @date 2022/4/26 17:02
 */
@Getter
@Setter
public class SmsAuthenticationParameter extends AuthenticationRequestParameter implements
        MankindTraits.ContactTraits.MobilePhoneNumber,
        GenericTraits.IdentifierTraits.Code<String> {
    /**
     * 用户名
     */
    @NotBlank
    private String mobilePhoneNumber;
    @NotBlank
    private String code;

    public SmsAuthenticationParameter(HttpServletRequest request) {
        super(request);
        this.mobilePhoneNumber = request.getParameter("mobilePhoneNumber");
        this.code = request.getParameter("code");
    }
}
