package com.jdcloud.gardener.camellia.uac.account.defaults.dao.mapper;

import com.jdcloud.gardener.camellia.uac.account.dao.mapper.AccountMapperTemplate;
import com.jdcloud.gardener.camellia.uac.account.defaults.dao.sql.DefaultAccountSqlProvider;
import com.jdcloud.gardener.camellia.uac.account.defaults.schema.criteria.DefaultAccountCriteria;
import com.jdcloud.gardener.camellia.uac.account.defaults.schema.entity.DefaultAccountEntity;
import com.jdcloud.gardener.fragrans.data.persistence.annotation.OverrideSqlProviderAnnotation;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zhanghan30
 * @date 2022/9/20 14:26
 */
@Mapper
@OverrideSqlProviderAnnotation(DefaultAccountSqlProvider.class)
public interface DefaultAccountMapper extends AccountMapperTemplate<DefaultAccountEntity, DefaultAccountCriteria> {
}
