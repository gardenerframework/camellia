package com.jdcloud.gardener.camellia.uac.account.atomic.verifier;

import com.jdcloud.gardener.camellia.uac.account.exception.client.AccountNotFoundException;
import com.jdcloud.gardener.camellia.uac.account.schema.entity.AccountEntityTemplate;
import com.jdcloud.gardener.fragrans.data.practice.operation.checker.BasicEntityExistenceChecker;
import com.jdcloud.gardener.fragrans.log.GenericLoggerStaticAccessor;
import lombok.experimental.SuperBuilder;

/**
 * @author zhanghan30
 * @date 2022/9/21 13:24
 */
@SuperBuilder
public class AccountMustExistVerifier extends BasicEntityExistenceChecker<String, AccountEntityTemplate> {
    @Override
    protected void init() {
        super.init();
        this.setExceptionFactory((ids, reason) -> new AccountNotFoundException(String.join(",", ids)));
        this.setBasicLogTemplate(GenericLoggerStaticAccessor.basicLogger()::debug);
    }
}
