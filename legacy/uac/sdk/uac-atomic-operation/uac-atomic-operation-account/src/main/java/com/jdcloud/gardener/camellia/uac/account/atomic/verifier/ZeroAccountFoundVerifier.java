package com.jdcloud.gardener.camellia.uac.account.atomic.verifier;

import com.jdcloud.gardener.camellia.uac.account.exception.client.AccountPropertyUniqueConstraintsViolationException;
import com.jdcloud.gardener.camellia.uac.account.schema.entity.AccountEntityTemplate;
import com.jdcloud.gardener.fragrans.data.practice.operation.checker.RecordCollectionChecker;
import lombok.experimental.SuperBuilder;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * 检查账号是否已经注册
 *
 * @author zhanghan30
 * @date 2022/9/16 10:37 下午
 */
@SuperBuilder
public class ZeroAccountFoundVerifier implements RecordCollectionChecker<AccountEntityTemplate> {

    @Override
    public <T extends AccountEntityTemplate> void check(Collection<T> records) {
        if (!CollectionUtils.isEmpty(records)) {
            throw new AccountPropertyUniqueConstraintsViolationException();
        }
    }
}
