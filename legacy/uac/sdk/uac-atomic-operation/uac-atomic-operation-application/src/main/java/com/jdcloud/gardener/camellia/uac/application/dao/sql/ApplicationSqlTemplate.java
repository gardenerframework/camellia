package com.jdcloud.gardener.camellia.uac.application.dao.sql;

import com.jdcloud.gardener.camellia.uac.application.dao.mapper.ApplicationMapperTemplate;
import com.jdcloud.gardener.camellia.uac.application.schema.criteria.ApplicationCriteriaTemplate;
import com.jdcloud.gardener.camellia.uac.application.schema.entity.ApplicationEntityTemplate;
import com.jdcloud.gardener.camellia.uac.common.dao.utils.PaginationUtils;
import com.jdcloud.gardener.fragrans.data.persistence.criteria.support.CriteriaBuilderStaticAccessor;
import com.jdcloud.gardener.fragrans.data.persistence.orm.entity.FieldScannerStaticAccessor;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.StatementBuilderStaticAccessor;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.annotation.TableNameUtils;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.column.Column;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.column.JsonObjectColumn;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.criteria.MatchAllCriteria;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.statement.SelectStatement;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.value.RawValue;
import com.jdcloud.gardener.fragrans.data.persistence.template.sql.DomainSqlTemplateBase;
import com.jdcloud.gardener.fragrans.data.practice.persistence.orm.statement.CommonScannerCallbacks;
import com.jdcloud.gardener.fragrans.data.practice.persistence.orm.statement.schema.criteria.CommonCriteria;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.builder.annotation.ProviderMethodResolver;
import org.springframework.lang.Nullable;

import java.util.Collection;

/**
 * @author zhanghan30
 * @date 2022/11/5 15:32
 */
public class ApplicationSqlTemplate<
        A extends ApplicationEntityTemplate,
        C extends ApplicationCriteriaTemplate
        > extends DomainSqlTemplateBase implements ProviderMethodResolver, ApplicationSqlApi<C> {
    public ApplicationSqlTemplate() {
        super(ApplicationMapperTemplate.class, ApplicationEntityTemplate.class);
    }

    public String createApplication(
            ProviderContext context,
            @Param(ApplicationMapperTemplate.APPLICATION_ENTITY_PARAMETER_NAME) A application) {
        return StatementBuilderStaticAccessor.builder().insert(
                getDomainObjectType(context),
                new CommonScannerCallbacks.InsertStatementIgnoredAnnotations(),
                ApplicationMapperTemplate.APPLICATION_ENTITY_PARAMETER_NAME
        ).build();
    }

    public String readApplication(
            ProviderContext context,
            @Param(ApplicationMapperTemplate.APPLICATION_ID_PARAMETER_NAME) String applicationId) {
        return StatementBuilderStaticAccessor.builder().select(
                        getDomainObjectType(context),
                        new CommonScannerCallbacks.SelectStatementIgnoredAnnotations()
                ).where(
                        new CommonCriteria.QueryByIdCriteria(
                                ApplicationMapperTemplate.APPLICATION_ID_PARAMETER_NAME
                        )
                )
                .build();
    }

    @SuppressWarnings("unchecked")
    public String searchApplication(
            ProviderContext context,
            @Param(ApplicationMapperTemplate.APPLICATION_CRITERIA_PARAMETER_NAME) C criteria,
            @Nullable Collection<Class<?>> must,
            @Nullable Collection<Class<?>> should,
            long pageNo,
            long pageSize
    ) {
        return createSearchApplicationStatementInternally(
                (Class<? extends ApplicationMapperTemplate<?, ?>>) context.getMapperType(),
                criteria,
                ApplicationMapperTemplate.APPLICATION_CRITERIA_PARAMETER_NAME,
                must,
                should,
                pageNo,
                pageSize
        ).countFoundRows(true).build();
    }

    public String updateApplication(ProviderContext context, @Param(ApplicationMapperTemplate.APPLICATION_ENTITY_PARAMETER_NAME) A application) {
        Class<?> entityType = getDomainObjectType(context);
        return StatementBuilderStaticAccessor.builder().update(
                entityType,
                new CommonScannerCallbacks.UpdateStatementIgnoredAnnotations(),
                ApplicationMapperTemplate.APPLICATION_ENTITY_PARAMETER_NAME
        ).where(
                new CommonCriteria.QueryByIdCriteria(
                        ApplicationMapperTemplate.APPLICATION_ENTITY_PARAMETER_NAME,
                        FieldScannerStaticAccessor.scanner().field(
                                entityType,
                                GenericTraits.Id.class
                        ))
        ).build();
    }

    public String changeApplicationEnableStatus(
            ProviderContext context,
            @Param(ApplicationMapperTemplate.APPLICATION_ID_PARAMETER_NAME) String applicationId, boolean status) {
        Class<?> entityType = getDomainObjectType(context);
        return StatementBuilderStaticAccessor.builder().update(entityType).column(
                FieldScannerStaticAccessor.scanner().column(
                        entityType,
                        GenericTraits.StatusTraits.EnableFlag.class
                ),
                column -> new RawValue<>(status)
        ).where(new CommonCriteria.QueryByIdCriteria(ApplicationMapperTemplate.APPLICATION_ID_PARAMETER_NAME)).build();
    }

    private SelectStatement createSearchApplicationStatementInternally(
            Class<? extends ApplicationMapperTemplate<?, ?>> mapperType,
            C criteria,
            String criteriaParameterName,
            @Nullable Collection<Class<?>> must,
            @Nullable Collection<Class<?>> should,
            long pageNo,
            long pageSize
    ) {
        Class<?> entityType = getDomainObjectType(mapperType);
        //先创建一个搜索所有应用的语句
        SelectStatement statement = StatementBuilderStaticAccessor.builder().select(
        ).columns(createSelectColumns(entityType)).table(TableNameUtils.getTableName(entityType));
        MatchAllCriteria queryCriteria = buildQueryCriteria(
                entityType, criteria, criteriaParameterName,
                must,
                should
        );
        if (!queryCriteria.isEmpty()) {
            statement.where(queryCriteria);
        }
        //附加分页参数
        PaginationUtils.appendPagination(statement, pageNo, pageSize);
        return statement;
    }

    /**
     * 创建select语句中的列
     *
     * @param entityType 实体类型
     * @return 列清单
     */
    private Collection<String> createSelectColumns(Class<?> entityType) {
        return new CommonScannerCallbacks.SelectStatementIgnoredAnnotations().apply(FieldScannerStaticAccessor.scanner(), entityType);
    }

    /**
     * 创建搜索条件
     *
     * @param entityType            实体类型
     * @param criteria              搜索参数
     * @param criteriaParameterName 搜索参数名
     * @param must                  必须的trait
     * @param should                可选的trait
     * @return 搜索条件
     */
    protected MatchAllCriteria buildQueryCriteria(
            Class<?> entityType,
            C criteria, String criteriaParameterName,
            @Nullable Collection<Class<?>> must,
            @Nullable Collection<Class<?>> should
    ) {
        return CriteriaBuilderStaticAccessor.builder().createCriteria(
                null,
                entityType,
                criteria,
                criteriaParameterName,
                must,
                should
        );

    }

    @Override
    public SelectStatement createSearchApplicationStatement(
            Class<? extends ApplicationMapperTemplate<?, ?>> mapperType,
            C criteria, String criteriaParameterName,
            @Nullable Collection<Class<?>> must,
            @Nullable Collection<Class<?>> should) {
        return createSearchApplicationStatementInternally(
                mapperType, criteria, criteriaParameterName, must, should, 1, Long.MAX_VALUE
        );
    }

    @Override
    public Column createIdColumn(Class<? extends ApplicationMapperTemplate<?, ?>> mapperType, String alias) {
        Class<?> entityType = getDomainObjectType(mapperType);
        return new Column(
                alias,
                FieldScannerStaticAccessor.scanner().column(
                        entityType,
                        GenericTraits.Id.class
                )
        );
    }

    @Override
    public JsonObjectColumn aggregateSelectColumnsToJson(Class<? extends ApplicationMapperTemplate<?, ?>> mapperType, String alias, String columnName) {
        Class<?> entityType = getDomainObjectType(mapperType);
        return new JsonObjectColumn(
                alias,
                createSelectColumns(entityType),
                FieldScannerStaticAccessor.scanner().getConverter(entityType)::columnToField,
                columnName
        );
    }
}
