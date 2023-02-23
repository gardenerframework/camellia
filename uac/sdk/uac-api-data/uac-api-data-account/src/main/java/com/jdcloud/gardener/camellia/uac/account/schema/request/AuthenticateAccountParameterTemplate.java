package com.jdcloud.gardener.camellia.uac.account.schema.request;

import com.jdcloud.gardener.camellia.uac.account.schema.request.constraints.PrincipalsNotAllBlank;
import com.jdcloud.gardener.fragrans.data.trait.account.AccountTraits;
import com.jdcloud.gardener.fragrans.data.trait.contact.ContactTraits;
import com.jdcloud.gardener.fragrans.data.trait.security.SecurityTraits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;


/**
 * 认证用户账户的请求参数
 * <p>
 * 给了常用的用户名、手机号，邮箱 + 密码的形式
 *
 * @author zhanghan30
 * @date 2022/8/13 11:06 上午
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@PrincipalsNotAllBlank
public class AuthenticateAccountParameterTemplate implements
        AccountTraits.Username,
        ContactTraits.MobilePhoneNumber,
        ContactTraits.Email,
        AccountTraits.Credentials,
        SecurityTraits.TuringTraits.CaptchaToken {
    /**
     * 用户名
     */
    private String username;
    /**
     * 手机号
     */
    private String mobilePhoneNumber;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 认证所需的密码
     * <p>
     * 但如果是短信登录，人脸登录等就没有这个参数了
     */
    @NotBlank
    private String password;
    /**
     * 人机验证码
     */
    private String captchaToken;
}
