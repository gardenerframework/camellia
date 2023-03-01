package com.jdcloud.gardener.camellia.uac.account.configuration;

/**
 * @author zhanghan30
 * @date 2022/11/15 12:22
 */

import com.jdcloud.gardener.camellia.uac.common.security.PasswordGenerator;
import com.jdcloud.gardener.fragrans.api.options.schema.ApiOption;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Max;

/**
 * 安全选项
 */
@ApiOption(readonly = false)
@NoArgsConstructor
@Getter
@Setter
public class AccountSecurityOption {
    /**
     * 密码有效期(天为单位)
     * <p>
     * 最大输入180天
     */
    @Max(180)
    @Nullable
    private Integer passwordValidityPeriod;

    private AccountSecurityOption.EmptyPasswordStrategy emptyPasswordStrategy = AccountSecurityOption.EmptyPasswordStrategy.GENERATE;

    public enum EmptyPasswordStrategy {
        /**
         * 接受
         * <p>
         * 用于创建或注册短信登录等非密码登录的用户
         */
        ACCEPT,
        /**
         * 生成
         * <p>
         * 通过{@link PasswordGenerator}生成一个密码
         */
        GENERATE;
    }
}