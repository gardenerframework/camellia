package com.jdcloud.gardener.camellia.authorization.authentication.main.schema.request;

import com.jdcloud.gardener.fragrans.data.trait.security.SecurityTraits;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;

/**
 * @author zhanghan30
 * @date 2022/4/25 7:17 下午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendSmsAuthenticationCodeRequest implements SecurityTraits.TuringTraits.CaptchaToken {
    /**
     * 手机号
     */
    @NotBlank
    private String mobilePhoneNumber;

    /**
     * 验证码(滑块什么的)
     */
    @Nullable
    private String captchaToken;
}
