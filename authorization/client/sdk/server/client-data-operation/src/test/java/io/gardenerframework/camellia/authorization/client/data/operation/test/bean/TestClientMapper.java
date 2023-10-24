package io.gardenerframework.camellia.authorization.client.data.operation.test.bean;

import io.gardenerframework.camellia.authorization.client.data.dao.mapper.ClientMapperTemplate;
import io.gardenerframework.fragrans.data.persistence.annotation.OverrideSqlProviderAnnotation;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author chris
 * @date 2023/10/24
 */
@Mapper
@OverrideSqlProviderAnnotation(TestClientSqlProvider.class)
public interface TestClientMapper extends ClientMapperTemplate<TestClientEntity, TestClientCriteria> {
}
