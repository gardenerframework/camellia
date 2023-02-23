package com.jdcloud.gardener.camellia.uac.test.utils;

import com.jdcloud.gardener.camellia.uac.account.schema.request.constraints.PasswordStrengthChecker;
import org.springframework.stereotype.Component;

/**
 * @author zhanghan30
 * @date 2022/11/15 22:39
 */
@Component
public class AlwaysStrongVerifier implements PasswordStrengthChecker {
    @Override
    public boolean check(CharSequence password) {
        return true;
    }
}
