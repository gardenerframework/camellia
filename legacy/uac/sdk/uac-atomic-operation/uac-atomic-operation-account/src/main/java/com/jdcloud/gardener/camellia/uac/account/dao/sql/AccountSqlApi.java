package com.jdcloud.gardener.camellia.uac.account.dao.sql;

import com.jdcloud.gardener.camellia.uac.account.dao.mapper.AccountMapperTemplate;
import com.jdcloud.gardener.camellia.uac.account.schema.criteria.AccountCriteriaTemplate;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.statement.SelectStatement;
import com.jdcloud.gardener.fragrans.data.persistence.template.sql.DomainSqlApi;
import org.springframework.lang.Nullable;

import java.util.Collection;

/**
 * @author zhanghan30
 * @date 2022/11/4 16:39
 */
public interface AccountSqlApi<C extends AccountCriteriaTemplate> extends DomainSqlApi {
    /**
     * 创建查询账户的sql语句
     *
     * @param criteria              查询条件
     * @param criteriaParameterName 条件参数名
     * @param must                  Trait清单，表达那些字段是必须满足
     * @param should                Trait清单，表达那些字段是可选满足
     * @return 语句
     */
    SelectStatement createSearchAccountStatement(
            Class<? extends AccountMapperTemplate<?, ?>> mapperType,
            C criteria,
            String criteriaParameterName,
            @Nullable Collection<Class<?>> must,
            @Nullable Collection<Class<?>> should
    );
}
