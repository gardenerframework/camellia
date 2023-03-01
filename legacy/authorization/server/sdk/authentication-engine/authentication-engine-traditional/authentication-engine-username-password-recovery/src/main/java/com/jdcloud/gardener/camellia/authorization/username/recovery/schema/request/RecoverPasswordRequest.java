package com.jdcloud.gardener.camellia.authorization.username.recovery.schema.request;

import com.jdcloud.gardener.fragrans.data.trait.account.AccountTraits;
import com.jdcloud.gardener.fragrans.data.trait.security.SecurityTraits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;

/**
 * 要求找回密码时发送的请求
 *
 * @author zhanghan30
 * @date 2022/1/10 12:21 下午
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecoverPasswordRequest implements
        SecurityTraits.TuringTraits.CaptchaToken,
        AccountTraits.IdentifierTraits.Username {
    /**
     * 找回的凭据名称
     * <p>
     * 如邮箱、手机号等
     * <p>
     * 密码保护这种东西现在没人用了
     */
    @NotBlank
    private String username;
    /**
     * 找回的凭据类型，如邮箱、手机号等
     * <p>
     * 提交的类型必须在后台有个转换器与之对应
     */
    @Nullable
    private String principalType;
    /**
     * 认证方法
     * <p>
     * 如果没有该参数，则使用前后端商议好的方法
     */
    @Nullable
    private String authenticator;
    /**
     * 验证码(滑块什么的)
     */
    @Nullable
    private String captchaToken;

}
