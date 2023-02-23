package com.jdcloud.gardener.camellia.authorization.authentication.main.schema.request;

import com.jdcloud.gardener.fragrans.data.trait.account.AccountTraits;
import com.jdcloud.gardener.fragrans.data.trait.security.SecurityTraits;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;

/**
 * 用户名密码登录参数
 *
 * @author ZhangHan
 * @date 2022/4/26 17:02
 */
@Getter
@Setter
public class UsernamePasswordAuthenticationParameter extends AbstractAuthenticationRequestParameter implements
        AccountTraits.IdentifierTraits.Username,
        SecurityTraits.SecretTraits.Password,
        SecurityTraits.TuringTraits.CaptchaToken {
    /**
     * 用户名
     */
    @NotBlank
    private String username;
    /**
     * 密码
     */
    @NotBlank
    private String password;
    /**
     * 登录名类型
     */
    @Nullable
    private String principalType;

    /**
     * 验证码(滑块什么得)
     */
    @Nullable
    private String captchaToken;


    public UsernamePasswordAuthenticationParameter(HttpServletRequest request) {
        super(request);
        this.username = request.getParameter("username");
        this.password = request.getParameter("password");
        this.principalType = request.getParameter("principalType");
        this.captchaToken = request.getParameter("captchaToken");
    }
}
