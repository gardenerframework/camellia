package com.jdcloud.gardener.camellia.uac.application.dao.sql;

import com.jdcloud.gardener.camellia.uac.application.dao.mapper.ApplicationMapperTemplate;
import com.jdcloud.gardener.camellia.uac.application.schema.criteria.ApplicationCriteriaTemplate;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.column.Column;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.column.JsonObjectColumn;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.statement.SelectStatement;
import com.jdcloud.gardener.fragrans.data.persistence.template.sql.DomainSqlApi;
import org.springframework.lang.Nullable;

import java.util.Collection;

/**
 * @author zhanghan30
 * @date 2022/11/5 16:13
 */
public interface ApplicationSqlApi<C extends ApplicationCriteriaTemplate> extends DomainSqlApi {
    /**
     * 创建搜索应用的sql语句，通常用于关联查询的子查询
     *
     * @param mapperType            mapper类型
     * @param criteria              搜索条件
     * @param criteriaParameterName 条件参数名
     * @param must                  必须包含的字段
     * @param should                可选包含的字段
     * @return 语句
     */
    SelectStatement createSearchApplicationStatement(
            Class<? extends ApplicationMapperTemplate<?, ?>> mapperType,
            C criteria,
            String criteriaParameterName,
            @Nullable Collection<Class<?>> must,
            @Nullable Collection<Class<?>> should
    );

    /**
     * 创建一个按id进行关联查询的列
     *
     * @param mapperType mapper类型
     * @param alias      关联查询中，分配给当前应用查询的结果别名
     * @return 列
     */
    Column createIdColumn(
            Class<? extends ApplicationMapperTemplate<?, ?>> mapperType,
            String alias
    );

    /**
     * 将select语句的column聚合为json object
     *
     * @param mapperType mapper类型
     * @param alias      关联查询中分配的别名
     * @param columnName json列的名
     * @return json列
     */
    JsonObjectColumn aggregateSelectColumnsToJson(
            Class<? extends ApplicationMapperTemplate<?, ?>> mapperType,
            String alias,
            String columnName
    );
}
