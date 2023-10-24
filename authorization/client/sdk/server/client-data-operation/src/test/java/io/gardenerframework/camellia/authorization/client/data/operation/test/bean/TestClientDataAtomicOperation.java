package io.gardenerframework.camellia.authorization.client.data.operation.test.bean;

import io.gardenerframework.camellia.authorization.client.data.dao.mapper.ClientMapperTemplate;
import io.gardenerframework.camellia.authorization.client.data.operation.ClientDataAtomicOperationTemplate;
import io.gardenerframework.fragrans.data.practice.operation.CommonOperations;
import lombok.NonNull;
import org.springframework.stereotype.Component;

/**
 * @author chris
 * @date 2023/10/24
 */
@Component
public class TestClientDataAtomicOperation extends ClientDataAtomicOperationTemplate<TestClientEntity, TestClientCriteria> {
    public TestClientDataAtomicOperation(@NonNull ClientMapperTemplate<TestClientEntity, TestClientCriteria> clientMapperTemplate, @NonNull CommonOperations commonOperations) {
        super(clientMapperTemplate, commonOperations);
    }
}
