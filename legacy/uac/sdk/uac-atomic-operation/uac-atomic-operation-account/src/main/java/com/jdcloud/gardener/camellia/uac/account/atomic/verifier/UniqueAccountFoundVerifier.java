package com.jdcloud.gardener.camellia.uac.account.atomic.verifier;

import com.jdcloud.gardener.camellia.uac.account.exception.client.AccountNotFoundOrNotUniqueException;
import com.jdcloud.gardener.camellia.uac.account.schema.entity.AccountEntityTemplate;
import com.jdcloud.gardener.fragrans.data.practice.operation.checker.RecordCollectionChecker;
import lombok.experimental.SuperBuilder;

import java.util.Collection;

/**
 * @author zhanghan30
 * @date 2022/9/21 13:24
 */
@SuperBuilder
public class UniqueAccountFoundVerifier implements RecordCollectionChecker<AccountEntityTemplate> {
    @Override
    public <T extends AccountEntityTemplate> void check(Collection<T> records) {
        if (records.size() != 1) {
            throw new AccountNotFoundOrNotUniqueException();
        }
    }
}
