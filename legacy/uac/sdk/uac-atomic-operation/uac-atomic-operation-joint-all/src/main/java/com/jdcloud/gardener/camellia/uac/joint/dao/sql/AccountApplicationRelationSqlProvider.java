package com.jdcloud.gardener.camellia.uac.joint.dao.sql;

import com.jdcloud.gardener.camellia.uac.account.dao.mapper.AccountMapperTemplate;
import com.jdcloud.gardener.camellia.uac.account.dao.sql.AccountSqlTemplate;
import com.jdcloud.gardener.camellia.uac.account.schema.criteria.AccountCriteriaTemplate;
import com.jdcloud.gardener.camellia.uac.account.schema.entity.AccountEntityTemplate;
import com.jdcloud.gardener.camellia.uac.application.dao.mapper.ApplicationMapperTemplate;
import com.jdcloud.gardener.camellia.uac.application.dao.sql.ApplicationSqlTemplate;
import com.jdcloud.gardener.camellia.uac.application.schema.criteria.ApplicationCriteriaTemplate;
import com.jdcloud.gardener.camellia.uac.application.schema.entity.ApplicationEntityTemplate;
import com.jdcloud.gardener.camellia.uac.application.schema.trait.ApplicationRelation;
import com.jdcloud.gardener.camellia.uac.common.dao.utils.PaginationUtils;
import com.jdcloud.gardener.camellia.uac.common.dao.utils.SqlProviderUtils;
import com.jdcloud.gardener.camellia.uac.common.schema.criteria.DomainCriteriaWrapper;
import com.jdcloud.gardener.camellia.uac.joint.dao.mapper.AccountApplicationRelationMapper;
import com.jdcloud.gardener.camellia.uac.joint.schema.aggregation.AccountApplicationAggregation;
import com.jdcloud.gardener.camellia.uac.joint.schema.entity.AccountApplicationRelation;
import com.jdcloud.gardener.camellia.uac.joint.schema.trait.JointTraits;
import com.jdcloud.gardener.fragrans.data.persistence.orm.entity.FieldScannerStaticAccessor;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.StatementBuilderStaticAccessor;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.annotation.TableNameUtils;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.column.Column;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.column.JsonObjectColumn;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.criteria.BooleanCriteria;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.criteria.EqualsCriteria;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.statement.SelectStatement;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.value.ParameterNameValue;
import com.jdcloud.gardener.fragrans.data.persistence.template.support.DomainDaoTemplateRegistry;
import com.jdcloud.gardener.fragrans.data.persistence.template.support.DomainObjectTemplateTypesResolver;
import com.jdcloud.gardener.fragrans.data.practice.persistence.orm.statement.CommonScannerCallbacks;
import com.jdcloud.gardener.fragrans.data.trait.account.AccountTraits;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.builder.annotation.ProviderMethodResolver;

import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zhanghan30
 * @date 2022/11/7 10:46
 */
public class AccountApplicationRelationSqlProvider implements ProviderMethodResolver {

    public String createRelation(@Param(AccountApplicationRelationMapper.ACCOUNT_APPLICATION_RELATION_PARAMETER_NAME) AccountApplicationRelation relation) {
        return StatementBuilderStaticAccessor.builder().insert(
                AccountApplicationRelation.class,
                new CommonScannerCallbacks.InsertStatementIgnoredAnnotations(),
                AccountApplicationRelationMapper.ACCOUNT_APPLICATION_RELATION_PARAMETER_NAME
        ).build();
    }


    public String changeExpiryDate(
            @Param(AccountApplicationRelationMapper.ACCOUNT_ID_PARAMETER_NAME) String accountId,
            @Param(AccountApplicationRelationMapper.APPLICATION_ID_PARAMETER_NAME) String applicationId,
            @Param(AccountApplicationRelationMapper.ACCOUNT_APPLICATION_RELATION_EXPIRY_DATE_PARAMETER_NAME) Date accountApplicationRelationExpiryDate) {
        return StatementBuilderStaticAccessor.builder().update(AccountApplicationRelation.class).column(
                FieldScannerStaticAccessor.scanner().column(
                        AccountApplicationRelation.class,
                        JointTraits.AccountApplicationRelationExpiryDate.class
                ),
                column -> new ParameterNameValue(AccountApplicationRelationMapper.ACCOUNT_APPLICATION_RELATION_EXPIRY_DATE_PARAMETER_NAME)
        ).where(
                new BooleanCriteria()
                        .a(new EqualsCriteria(
                                FieldScannerStaticAccessor.scanner().column(
                                        AccountApplicationRelation.class,
                                        AccountTraits.AccountRelation.class
                                ),
                                new ParameterNameValue(AccountApplicationRelationMapper.ACCOUNT_ID_PARAMETER_NAME)
                        ))
                        .and()
                        .b(new EqualsCriteria(
                                FieldScannerStaticAccessor.scanner().column(
                                        AccountApplicationRelation.class,
                                        ApplicationRelation.class
                                ),
                                new ParameterNameValue(AccountApplicationRelationMapper.APPLICATION_ID_PARAMETER_NAME)
                        ))
        ).build();
    }

    public String readRelation(@Param(AccountApplicationRelationMapper.ACCOUNT_ID_PARAMETER_NAME) String accountId, @Param(ApplicationMapperTemplate.APPLICATION_ID_PARAMETER_NAME) String applicationId) {
        return StatementBuilderStaticAccessor.builder().select(
                AccountApplicationRelation.class,
                new CommonScannerCallbacks.SelectStatementIgnoredAnnotations()
        ).where(
                new BooleanCriteria()
                        .a(
                                new EqualsCriteria(
                                        FieldScannerStaticAccessor.scanner().column(
                                                AccountApplicationRelation.class,
                                                AccountTraits.AccountRelation.class
                                        ),
                                        new ParameterNameValue(AccountApplicationRelationMapper.ACCOUNT_ID_PARAMETER_NAME)
                                )

                        ).and()
                        .b(
                                new EqualsCriteria(
                                        FieldScannerStaticAccessor.scanner().column(
                                                AccountApplicationRelation.class,
                                                ApplicationRelation.class
                                        ),
                                        new ParameterNameValue(AccountApplicationRelationMapper.APPLICATION_ID_PARAMETER_NAME)
                                )
                        )
        ).build();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public String aggregationSearch(
            @Param(AccountApplicationRelationMapper.ACCOUNT_CRITERIA_PARAMETER_NAME) DomainCriteriaWrapper<? extends AccountCriteriaTemplate> accountCriteria,
            @Param(AccountApplicationRelationMapper.APPLICATION_CRITERIA_PARAMETER_NAME) DomainCriteriaWrapper<? extends ApplicationCriteriaTemplate> applicationCriteria,
            long pageNo,
            long pageSize
    ) {
        //获取当前激活的mapper
        Class<? extends AccountMapperTemplate> accountMapper = Objects.requireNonNull(DomainDaoTemplateRegistry.getItem(AccountMapperTemplate.class)).getActiveImplementation();
        Class<? extends ApplicationMapperTemplate> applicationMapper = Objects.requireNonNull(DomainDaoTemplateRegistry.getItem(ApplicationMapperTemplate.class)).getActiveImplementation();
        //获取激活的sql provider
        AccountSqlTemplate accountSqlTemplate = SqlProviderUtils.getActiveProvider(accountMapper);
        ApplicationSqlTemplate applicationSqlTemplate = SqlProviderUtils.getActiveProvider(applicationMapper);
        //查询关系表
        String mainTableName = TableNameUtils.getTableName(AccountApplicationRelation.class);
        SelectStatement statement = StatementBuilderStaticAccessor.builder().select(
                AccountApplicationRelation.class,
                new CommonScannerCallbacks.SelectStatementIgnoredAnnotations(),
                true
        ).countFoundRows(true);
        //联合查询账户表
        String accountResultSetName = "account";
        SelectStatement searchAccountStatement = accountSqlTemplate.createSearchAccountStatement(
                accountMapper,
                accountCriteria.getCriteria(),
                String.format("%s.%s", AccountApplicationRelationMapper.ACCOUNT_CRITERIA_PARAMETER_NAME, accountCriteria.getCriteriaFieldName()),
                accountCriteria.getMust(),
                accountCriteria.getShould()
        );
        statement.join(searchAccountStatement, accountResultSetName).on(
                new EqualsCriteria(
                        new Column(
                                mainTableName,
                                FieldScannerStaticAccessor.scanner().column(
                                        AccountApplicationRelation.class,
                                        AccountTraits.AccountRelation.class
                                )
                        ),
                        new Column(
                                accountResultSetName,
                                FieldScannerStaticAccessor.scanner().column(
                                        accountSqlTemplate.getDomainObjectType(accountMapper),
                                        GenericTraits.Id.class
                                )
                        )
                )
        );
        //account记录转为json
        statement.column(
                new JsonObjectColumn(
                        accountResultSetName,
                        searchAccountStatement.getQueryColumns().stream().map(Column::getColumnName).collect(Collectors.toList()),
                        column -> FieldScannerStaticAccessor.scanner().getConverter(
                                DomainObjectTemplateTypesResolver.resolveTemplateImplementationType(
                                        accountMapper,
                                        AccountMapperTemplate.class,
                                        AccountEntityTemplate.class
                                )
                        ).columnToField(column),
                        FieldScannerStaticAccessor.scanner().column(
                                AccountApplicationAggregation.class,
                                JointTraits.AggregationTraits.Account.class
                        )
                )
        );
        //关联查询应用表
        String applicationResultSetName = "application";
        SelectStatement searchApplicationStatement = applicationSqlTemplate.createSearchApplicationStatement(
                applicationMapper,
                applicationCriteria.getCriteria(),
                String.format("%s.%s", AccountApplicationRelationMapper.APPLICATION_CRITERIA_PARAMETER_NAME, applicationCriteria.getCriteriaFieldName()),
                applicationCriteria.getMust(),
                applicationCriteria.getShould()
        );
        statement.join(searchApplicationStatement, applicationResultSetName).on(
                new EqualsCriteria(
                        new Column(
                                mainTableName,
                                FieldScannerStaticAccessor.scanner().column(
                                        AccountApplicationRelation.class,
                                        ApplicationRelation.class
                                )
                        ),
                        new Column(
                                applicationResultSetName,
                                FieldScannerStaticAccessor.scanner().column(
                                        applicationSqlTemplate.getDomainObjectType(applicationMapper),
                                        GenericTraits.Id.class
                                )
                        )
                )
        );
        //应用记录转为json
        statement.column(
                new JsonObjectColumn(
                        applicationResultSetName,
                        searchApplicationStatement.getQueryColumns().stream().map(Column::getColumnName).collect(Collectors.toList()),
                        column -> FieldScannerStaticAccessor.scanner().getConverter(
                                DomainObjectTemplateTypesResolver.resolveTemplateImplementationType(
                                        applicationMapper,
                                        ApplicationMapperTemplate.class,
                                        ApplicationEntityTemplate.class
                                )
                        ).columnToField(column),
                        FieldScannerStaticAccessor.scanner().column(
                                AccountApplicationAggregation.class,
                                JointTraits.AggregationTraits.Application.class
                        )
                )
        );
        //附加上分页条件
        PaginationUtils.appendPagination(statement, pageNo, pageSize);
        return statement.build();
    }
}
