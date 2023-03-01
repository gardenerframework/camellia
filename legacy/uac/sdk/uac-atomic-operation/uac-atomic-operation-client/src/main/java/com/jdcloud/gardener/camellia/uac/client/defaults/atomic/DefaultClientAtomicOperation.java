package com.jdcloud.gardener.camellia.uac.client.defaults.atomic;

import com.jdcloud.gardener.camellia.uac.client.atomic.ClientAtomicOperationTemplate;
import com.jdcloud.gardener.camellia.uac.client.dao.mapper.ClientMapperTemplate;
import com.jdcloud.gardener.camellia.uac.client.defaults.schema.criteria.DefaultClientCriteria;
import com.jdcloud.gardener.camellia.uac.client.defaults.schema.entity.DefaultClientEntity;
import com.jdcloud.gardener.fragrans.data.practice.operation.CommonOperations;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

/**
 * @author ZhangHan
 * @date 2022/11/24 16:40
 */
@ConditionalOnMissingBean(value = ClientAtomicOperationTemplate.class, ignored = DefaultClientAtomicOperation.class)
public class DefaultClientAtomicOperation extends ClientAtomicOperationTemplate<DefaultClientEntity, DefaultClientCriteria> {
    public DefaultClientAtomicOperation(ClientMapperTemplate<DefaultClientEntity, DefaultClientCriteria> clientMapper, CommonOperations commonOperations) {
        super(clientMapper, commonOperations);
    }
}
