package com.jdcloud.gardener.camellia.uac.account.defaults.atomic;

import com.jdcloud.gardener.camellia.uac.account.atomic.AccountAtomicOperationTemplate;
import com.jdcloud.gardener.camellia.uac.account.defaults.schema.criteria.DefaultAccountCriteria;
import com.jdcloud.gardener.camellia.uac.account.defaults.schema.entity.DefaultAccountEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

/**
 * @author zhanghan30
 * @date 2022/11/15 20:49
 */
@ConditionalOnMissingBean(value = AccountAtomicOperationTemplate.class, ignored = DefaultAccountAtomicOperation.class)
public class DefaultAccountAtomicOperation extends AccountAtomicOperationTemplate<DefaultAccountEntity, DefaultAccountCriteria> {
}
