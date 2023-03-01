package com.jdcloud.gardener.camellia.uac.client.defaults.dao.mapper;

import com.jdcloud.gardener.camellia.uac.client.dao.mapper.ClientMapperTemplate;
import com.jdcloud.gardener.camellia.uac.client.defaults.dao.sql.DefaultClientSqlProvider;
import com.jdcloud.gardener.camellia.uac.client.defaults.schema.criteria.DefaultClientCriteria;
import com.jdcloud.gardener.camellia.uac.client.defaults.schema.entity.DefaultClientEntity;
import com.jdcloud.gardener.fragrans.data.persistence.annotation.OverrideSqlProviderAnnotation;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zhanghan30
 * @date 2022/11/14 13:39
 */
@Mapper
@OverrideSqlProviderAnnotation(DefaultClientSqlProvider.class)
public interface DefaultClientMapper extends ClientMapperTemplate<DefaultClientEntity, DefaultClientCriteria> {
}
