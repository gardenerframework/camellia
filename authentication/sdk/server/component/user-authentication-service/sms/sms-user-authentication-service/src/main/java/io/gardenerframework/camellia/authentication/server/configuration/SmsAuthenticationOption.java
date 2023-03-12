package io.gardenerframework.camellia.authentication.server.configuration;

import io.gardenerframework.fragrans.api.options.schema.ApiOption;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiOption(readonly = false)
@SmsAuthenticationServiceComponent
@Getter
@Setter
@NoArgsConstructor
public class SmsAuthenticationOption {
    /**
     * 验证码的cd时间
     */
    private int verificationCodeCooldown = 60;
    /**
     * 验证码的有效期，默认是300秒
     */
    private long verificationCodeTtl = 300;
}
