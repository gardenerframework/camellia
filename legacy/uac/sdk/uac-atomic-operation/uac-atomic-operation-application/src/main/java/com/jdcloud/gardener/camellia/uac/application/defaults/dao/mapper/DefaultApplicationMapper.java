package com.jdcloud.gardener.camellia.uac.application.defaults.dao.mapper;

import com.jdcloud.gardener.camellia.uac.application.dao.mapper.ApplicationMapperTemplate;
import com.jdcloud.gardener.camellia.uac.application.defaults.dao.sql.DefaultApplicationSqlProvider;
import com.jdcloud.gardener.camellia.uac.application.defaults.schema.criteria.DefaultApplicationCriteria;
import com.jdcloud.gardener.camellia.uac.application.defaults.schema.entity.DefaultApplicationEntity;
import com.jdcloud.gardener.fragrans.data.persistence.annotation.OverrideSqlProviderAnnotation;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zhanghan30
 * @date 2022/11/7 13:37
 */
@Mapper
@OverrideSqlProviderAnnotation(DefaultApplicationSqlProvider.class)
public interface DefaultApplicationMapper extends ApplicationMapperTemplate<DefaultApplicationEntity, DefaultApplicationCriteria> {
}
