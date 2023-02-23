package com.jdcloud.gardener.camellia.uac.application.defaults.atomic;

import com.jdcloud.gardener.camellia.uac.application.atomic.ApplicationAtomicOperationTemplate;
import com.jdcloud.gardener.camellia.uac.application.defaults.schema.criteria.DefaultApplicationCriteria;
import com.jdcloud.gardener.camellia.uac.application.defaults.schema.entity.DefaultApplicationEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

/**
 * @author zhanghan30
 * @date 2022/11/7 11:56
 */
@ConditionalOnMissingBean(value = ApplicationAtomicOperationTemplate.class, ignored = DefaultApplicationAtomicOperation.class)
public class DefaultApplicationAtomicOperation extends ApplicationAtomicOperationTemplate<DefaultApplicationEntity, DefaultApplicationCriteria> {
}
