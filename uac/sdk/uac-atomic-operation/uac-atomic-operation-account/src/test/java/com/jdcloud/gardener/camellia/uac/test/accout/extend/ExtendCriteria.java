package com.jdcloud.gardener.camellia.uac.test.accout.extend;

import com.jdcloud.gardener.camellia.uac.account.schema.criteria.AccountCriteriaTemplate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author zhanghan30
 * @date 2022/9/24 12:08
 */
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class ExtendCriteria extends AccountCriteriaTemplate implements TestTrait {
    private String test;
}
