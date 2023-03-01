package com.jdcloud.gardener.camellia.authorization.authentication.configuration;

import com.jdcloud.gardener.fragrans.api.options.schema.ApiOption;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@ApiOption(readonly = false)
@Getter
@Setter
@NoArgsConstructor
public class UsernamePasswordAuthenticationSecurityOption {
    /**
     * 加密秘钥的有效期
     * <p>
     * 默认5分钟
     */
    @NotNull
    @Positive
    private Long encryptionKeyTtl = 300L;
    /**
     * 返回数据中是否显示加密算法
     * <p>
     * 默认不显示，否则可能导致加密方法被泄露
     */
    private boolean showAlgorithm = false;
}
