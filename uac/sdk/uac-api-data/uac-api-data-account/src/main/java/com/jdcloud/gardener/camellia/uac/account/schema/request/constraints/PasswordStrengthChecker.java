package com.jdcloud.gardener.camellia.uac.account.schema.request.constraints;

/**
 * @author zhanghan30
 * @date 2022/11/15 22:44
 */
@FunctionalInterface
public interface PasswordStrengthChecker {
    /**
     * 检查当前密码的强度是否足够
     *
     * @param password 密码
     * @return 是否足够
     */
    boolean check(CharSequence password);
}
