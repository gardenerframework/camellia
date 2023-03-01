package com.jdcloud.gardener.camellia.uac.test.accout.extend;

import com.jdcloud.gardener.camellia.uac.account.atomic.AccountAtomicOperationTemplate;
import org.springframework.stereotype.Component;

/**
 * @author zhanghan30
 * @date 2022/11/15 20:49
 */
@Component
public class ExtendAccountAtomicOperation extends AccountAtomicOperationTemplate<ExtendAccount, ExtendCriteria> {
}
