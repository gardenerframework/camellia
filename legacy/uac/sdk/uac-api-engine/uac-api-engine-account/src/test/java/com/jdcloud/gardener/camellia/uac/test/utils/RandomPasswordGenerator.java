package com.jdcloud.gardener.camellia.uac.test.utils;

import com.jdcloud.gardener.camellia.uac.account.schema.entity.AccountEntityTemplate;
import com.jdcloud.gardener.camellia.uac.common.security.PasswordGenerator;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author ZhangHan
 * @date 2022/11/8 12:37
 */
@Component
public class RandomPasswordGenerator implements PasswordGenerator<AccountEntityTemplate> {
    @Override
    public String generate(AccountEntityTemplate entity) {
        return UUID.randomUUID().toString();
    }
}
