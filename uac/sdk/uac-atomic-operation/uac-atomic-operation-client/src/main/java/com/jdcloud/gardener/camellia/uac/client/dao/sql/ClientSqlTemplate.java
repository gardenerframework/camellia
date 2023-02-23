package com.jdcloud.gardener.camellia.uac.client.dao.sql;

import com.jdcloud.gardener.camellia.uac.client.dao.mapper.ClientMapperTemplate;
import com.jdcloud.gardener.camellia.uac.client.schema.criteria.ClientCriteriaTemplate;
import com.jdcloud.gardener.camellia.uac.client.schema.entity.ClientEntityTemplate;
import com.jdcloud.gardener.camellia.uac.client.schema.trait.GrantType;
import com.jdcloud.gardener.camellia.uac.client.schema.trait.RedirectUri;
import com.jdcloud.gardener.camellia.uac.client.schema.trait.RequireConsentFlag;
import com.jdcloud.gardener.camellia.uac.client.schema.trait.Scope;
import com.jdcloud.gardener.camellia.uac.common.dao.utils.PaginationUtils;
import com.jdcloud.gardener.camellia.uac.common.dao.utils.QueryCriteriaUtils;
import com.jdcloud.gardener.fragrans.data.persistence.orm.entity.FieldScannerStaticAccessor;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.StatementBuilderStaticAccessor;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.annotation.TableNameUtils;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.column.Column;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.column.JsonObjectColumn;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.criteria.BasicCriteria;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.criteria.MatchAllCriteria;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.statement.SelectStatement;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.value.ParameterNameValue;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.value.RawValue;
import com.jdcloud.gardener.fragrans.data.persistence.template.sql.DomainSqlTemplateBase;
import com.jdcloud.gardener.fragrans.data.practice.persistence.orm.statement.CommonScannerCallbacks;
import com.jdcloud.gardener.fragrans.data.practice.persistence.orm.statement.schema.criteria.CommonCriteria;
import com.jdcloud.gardener.fragrans.data.trait.account.AccountTraits;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.builder.annotation.ProviderMethodResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ZhangHan
 * @date 2022/11/12 0:22
 */
public class ClientSqlTemplate<
        T extends ClientEntityTemplate,
        C extends ClientCriteriaTemplate
        > extends DomainSqlTemplateBase implements ProviderMethodResolver, ClientSqlApi<C> {
    public ClientSqlTemplate() {
        super(ClientMapperTemplate.class, ClientEntityTemplate.class);
    }

    public String createClient(ProviderContext context, @Param(ClientMapperTemplate.CLIENT_ENTITY_PARAMETER_NAME) T client) {
        return StatementBuilderStaticAccessor.builder().insert(
                getDomainObjectType(context),
                new CommonScannerCallbacks.InsertStatementIgnoredAnnotations(),
                ClientMapperTemplate.CLIENT_ENTITY_PARAMETER_NAME
        ).build();
    }


    public String readClient(ProviderContext context, @Param(ClientMapperTemplate.CLIENT_ID_PARAMETER_NAME) String clientId, boolean showPassword) {
        CommonScannerCallbacks.CompositeCallbacks compositeCallbacks = new CommonScannerCallbacks.CompositeCallbacks();
        //包含的列
        compositeCallbacks.include(new CommonScannerCallbacks.SelectStatementIgnoredAnnotations());
        if (!showPassword) {
            //包含基础上去掉的列
            compositeCallbacks.exclude(
                    new CommonScannerCallbacks.UsingTraits(Collections.singletonList(AccountTraits.Credentials.class))
            );
        }
        return StatementBuilderStaticAccessor.builder().select(
                        getDomainObjectType(context),
                        compositeCallbacks
                ).where(
                        new CommonCriteria.QueryByIdCriteria(
                                ClientMapperTemplate.CLIENT_ID_PARAMETER_NAME
                        )
                )
                .build();
    }

    @SuppressWarnings("unchecked")
    public String searchClient(
            ProviderContext context,
            @Param(ClientMapperTemplate.CLIENT_CRITERIA_PARAMETER_NAME) C criteria,
            @Nullable Collection<Class<?>> must,
            @Nullable Collection<Class<?>> should,
            long pageNo,
            long pageSize
    ) {
        return createSearchClientStatementInternally(
                (Class<? extends ClientMapperTemplate<?, ?>>) context.getMapperType(),
                criteria,
                ClientMapperTemplate.CLIENT_CRITERIA_PARAMETER_NAME,
                must,
                should,
                pageNo,
                pageSize
        ).countFoundRows(true).build();
    }


    public String updateClient(ProviderContext context, @Param(ClientMapperTemplate.CLIENT_ENTITY_PARAMETER_NAME) T client) {
        Class<?> entityType = getDomainObjectType(context);
        return StatementBuilderStaticAccessor.builder().update(
                entityType,
                new CommonScannerCallbacks.UpdateStatementIgnoredAnnotations(),
                ClientMapperTemplate.CLIENT_ENTITY_PARAMETER_NAME
        ).where(
                new CommonCriteria.QueryByIdCriteria(
                        ClientMapperTemplate.CLIENT_ENTITY_PARAMETER_NAME,
                        FieldScannerStaticAccessor.scanner().field(
                                entityType,
                                GenericTraits.Id.class
                        ))
        ).build();
    }

    public String changeClientEnableStatus(
            ProviderContext context,
            @Param(ClientMapperTemplate.CLIENT_ID_PARAMETER_NAME) String clientId,
            boolean status
    ) {
        Class<?> entityType = getDomainObjectType(context);
        return StatementBuilderStaticAccessor.builder().update(entityType).column(
                FieldScannerStaticAccessor.scanner().column(
                        entityType,
                        GenericTraits.StatusTraits.EnableFlag.class
                ),
                column -> new RawValue<>(status)
        ).where(new CommonCriteria.QueryByIdCriteria(ClientMapperTemplate.CLIENT_ID_PARAMETER_NAME)).build();
    }

    public String changeClientRequireConsentFlag(
            ProviderContext context,
            @Param(ClientMapperTemplate.CLIENT_ID_PARAMETER_NAME) String clientId,
            boolean flag
    ) {
        Class<?> entityType = getDomainObjectType(context);
        return StatementBuilderStaticAccessor.builder().update(entityType).column(
                FieldScannerStaticAccessor.scanner().column(
                        entityType,
                        RequireConsentFlag.class
                ),
                column -> new RawValue<>(flag)
        ).where(new CommonCriteria.QueryByIdCriteria(ClientMapperTemplate.CLIENT_ID_PARAMETER_NAME)).build();
    }

    public String changeClientScope(
            ProviderContext context,
            @Param(ClientMapperTemplate.CLIENT_ID_PARAMETER_NAME) String clientId,
            @Param(ClientMapperTemplate.CLIENT_SCOPE_PARAMETER_NAME) Collection<String> scope
    ) {
        Class<?> entityType = getDomainObjectType(context);
        return StatementBuilderStaticAccessor.builder().update(entityType).column(
                FieldScannerStaticAccessor.scanner().column(
                        entityType,
                        Scope.class
                ),
                column -> new ParameterNameValue(ClientMapperTemplate.CLIENT_SCOPE_PARAMETER_NAME)
        ).where(new CommonCriteria.QueryByIdCriteria(ClientMapperTemplate.CLIENT_ID_PARAMETER_NAME)).build();
    }

    public String changeClientGrantType(
            ProviderContext context,
            @Param(ClientMapperTemplate.CLIENT_ID_PARAMETER_NAME) String clientId,
            @Param(ClientMapperTemplate.CLIENT_GRANT_TYPE_PARAMETER_NAME) Collection<String> grantType
    ) {
        Class<?> entityType = getDomainObjectType(context);
        return StatementBuilderStaticAccessor.builder().update(entityType).column(
                FieldScannerStaticAccessor.scanner().column(
                        entityType,
                        GrantType.class
                ),
                column -> new ParameterNameValue(ClientMapperTemplate.CLIENT_GRANT_TYPE_PARAMETER_NAME)
        ).where(new CommonCriteria.QueryByIdCriteria(ClientMapperTemplate.CLIENT_ID_PARAMETER_NAME)).build();
    }

    public String changeClientRedirectUri(
            ProviderContext context,
            @Param(ClientMapperTemplate.CLIENT_ID_PARAMETER_NAME) String clientId,
            @Param(ClientMapperTemplate.CLIENT_REDIRECT_URI_PARAMETER_NAME) Collection<String> redirectUri
    ) {
        Class<?> entityType = getDomainObjectType(context);
        return StatementBuilderStaticAccessor.builder().update(entityType).column(
                FieldScannerStaticAccessor.scanner().column(
                        entityType,
                        RedirectUri.class
                ),
                column -> new ParameterNameValue(ClientMapperTemplate.CLIENT_REDIRECT_URI_PARAMETER_NAME)
        ).where(new CommonCriteria.QueryByIdCriteria(ClientMapperTemplate.CLIENT_ID_PARAMETER_NAME)).build();
    }

    private SelectStatement createSearchClientStatementInternally(
            Class<? extends ClientMapperTemplate<?, ?>> mapperType,
            C criteria, String criteriaParameterName,
            @Nullable Collection<Class<?>> must,
            @Nullable Collection<Class<?>> should,
            long pageNo, long pageSize
    ) {
        Class<?> entityType = getDomainObjectType(mapperType);
        SelectStatement statement = StatementBuilderStaticAccessor.builder().select().table(TableNameUtils.getTableName(entityType)).columns(createSelectColumns(entityType));
        MatchAllCriteria queryCriteria = QueryCriteriaUtils.createQueryCriteria(
                () -> buildQueryCriteria(entityType, criteria, criteriaParameterName),
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
        return new CommonScannerCallbacks.CompositeCallbacks().include(
                new CommonScannerCallbacks.SelectStatementIgnoredAnnotations()
        ).exclude(
                //不显示密码
                new CommonScannerCallbacks.UsingTraits(
                        Collections.singletonList(
                                AccountTraits.Credentials.class
                        )
                )
        ).apply(FieldScannerStaticAccessor.scanner(), entityType);
    }

    protected Map<Class<?>, BasicCriteria> buildQueryCriteria(
            Class<?> entityType,
            C criteria, String criteriaParameterName
    ) {
        Map<Class<?>, BasicCriteria> mappings = new HashMap<>();
        if (StringUtils.hasText(criteria.getId())) {
            QueryCriteriaUtils.addEqualsCriteria(
                    mappings,
                    entityType,
                    criteriaParameterName,
                    GenericTraits.Id.class
            );
        }
        if (StringUtils.hasText(criteria.getName())) {
            QueryCriteriaUtils.addEqualsCriteria(
                    mappings,
                    entityType,
                    criteriaParameterName,
                    GenericTraits.Name.class
            );
        }
        return mappings;
    }

    @Override
    public SelectStatement createSearchClientStatement(Class<? extends ClientMapperTemplate<?, ?>> mapperType, C criteria, String criteriaParameterName, @Nullable Collection<Class<?>> must, @Nullable Collection<Class<?>> should) {
        return createSearchClientStatementInternally(
                mapperType,
                criteria,
                criteriaParameterName,
                must,
                should,
                1,
                Long.MAX_VALUE
        );
    }

    @Override
    public Column createIdColumn(Class<? extends ClientMapperTemplate<?, ?>> mapperType, String alias) {
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
    public JsonObjectColumn aggregateSelectColumnsToJson(Class<? extends ClientMapperTemplate<?, ?>> mapperType, String alias, String columnName) {
        Class<?> entityType = getDomainObjectType(mapperType);
        return new JsonObjectColumn(
                alias,
                createSelectColumns(entityType),
                FieldScannerStaticAccessor.scanner().getConverter(entityType)::columnToField,
                columnName
        );
    }
}
