package com.jdcloud.gardener.camellia.uac.account.dao.sql;

import com.jdcloud.gardener.camellia.uac.account.dao.mapper.AccountMapperTemplate;
import com.jdcloud.gardener.camellia.uac.account.schema.criteria.AccountCriteriaTemplate;
import com.jdcloud.gardener.camellia.uac.account.schema.entity.AccountEntityTemplate;
import com.jdcloud.gardener.camellia.uac.common.dao.utils.PaginationUtils;
import com.jdcloud.gardener.fragrans.data.persistence.criteria.support.CriteriaBuilderStaticAccessor;
import com.jdcloud.gardener.fragrans.data.persistence.orm.entity.FieldScannerStaticAccessor;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.StatementBuilderStaticAccessor;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.criteria.MatchAllCriteria;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.statement.SelectStatement;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.value.ParameterNameValue;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.value.RawValue;
import com.jdcloud.gardener.fragrans.data.persistence.template.sql.DomainSqlTemplateBase;
import com.jdcloud.gardener.fragrans.data.practice.persistence.orm.statement.CommonScannerCallbacks;
import com.jdcloud.gardener.fragrans.data.practice.persistence.orm.statement.schema.criteria.CommonCriteria;
import com.jdcloud.gardener.fragrans.data.trait.account.AccountTraits;
import com.jdcloud.gardener.fragrans.data.trait.contact.ContactTraits;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.builder.annotation.ProviderMethodResolver;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

/**
 * 账户实体的dao模板的sql provider
 *
 * @author zhanghan30
 * @date 2022/9/20 12:53
 */
public class AccountSqlTemplate<A extends AccountEntityTemplate, C extends AccountCriteriaTemplate>
        extends DomainSqlTemplateBase
        implements ProviderMethodResolver, AccountSqlApi<C> {

    public AccountSqlTemplate() {
        super(AccountMapperTemplate.class, AccountEntityTemplate.class);
    }

    public String createAccount(ProviderContext context, @Param(AccountMapperTemplate.ACCOUNT_ENTITY_PARAMETER_NAME) A account) {
        return StatementBuilderStaticAccessor.builder().insert(
                getDomainObjectType(context),
                new CommonScannerCallbacks.InsertStatementIgnoredAnnotations(),
                AccountMapperTemplate.ACCOUNT_ENTITY_PARAMETER_NAME
        ).build();
    }

    public String readAccount(ProviderContext context, @Param(AccountMapperTemplate.ACCOUNT_ID_PARAMETER_NAME) String accountId, boolean showPassword) {
        CommonScannerCallbacks.CompositeCallbacks compositeCallbacks = new CommonScannerCallbacks.CompositeCallbacks();
        //包含的列
        compositeCallbacks.include(new CommonScannerCallbacks.SelectStatementIgnoredAnnotations());
        if (!showPassword) {
            //包含基础上去掉的列
            compositeCallbacks.exclude(
                    new CommonScannerCallbacks.UsingTraits(
                            Arrays.asList(
                                    AccountTraits.Credentials.class,
                                    AccountTraits.CredentialsExpiryDate.class
                            )
                    )
            );
        }
        return StatementBuilderStaticAccessor.builder().select(
                        getDomainObjectType(context),
                        compositeCallbacks
                ).where(new CommonCriteria.QueryByIdCriteria(AccountMapperTemplate.ACCOUNT_ID_PARAMETER_NAME))
                .build();
    }


    public String changeAccountLockStatus(ProviderContext context, @Param(AccountMapperTemplate.ACCOUNT_ID_PARAMETER_NAME) String accountId, boolean status) {
        Class<?> accountType = getDomainObjectType(context);
        return StatementBuilderStaticAccessor.builder().update(accountType).column(
                FieldScannerStaticAccessor.scanner().column(accountType, GenericTraits.StatusTraits.LockFlag.class), column -> new RawValue<>(status)
        ).where(new CommonCriteria.QueryByIdCriteria(AccountMapperTemplate.ACCOUNT_ID_PARAMETER_NAME)).build();
    }


    public String changeAccountEnableStatus(ProviderContext context, @Param(AccountMapperTemplate.ACCOUNT_ID_PARAMETER_NAME) String accountId, boolean status) {
        Class<?> accountType = getDomainObjectType(context);
        return StatementBuilderStaticAccessor.builder().update(accountType).column(
                FieldScannerStaticAccessor.scanner().column(accountType, GenericTraits.StatusTraits.EnableFlag.class), column -> new RawValue<>(status)
        ).where(new CommonCriteria.QueryByIdCriteria(AccountMapperTemplate.ACCOUNT_ID_PARAMETER_NAME)).build();
    }


    public String changePassword(
            ProviderContext context,
            @Param(AccountMapperTemplate.ACCOUNT_ID_PARAMETER_NAME) String accountId,
            @Param(AccountMapperTemplate.PASSWORD_PARAMETER_NAME) String password,
            @Param(AccountMapperTemplate.PASSWORD_EXPIRY_DATE_PARAMETER_NAME) @Nullable Date passwordExpiryDate
    ) {
        Class<?> accountType = getDomainObjectType(context);
        return StatementBuilderStaticAccessor.builder().update(accountType).column(
                FieldScannerStaticAccessor.scanner().column(accountType, AccountTraits.Credentials.class),
                column -> new ParameterNameValue(AccountMapperTemplate.PASSWORD_PARAMETER_NAME)
        ).column(
                FieldScannerStaticAccessor.scanner().column(accountType, AccountTraits.CredentialsExpiryDate.class),
                column -> new ParameterNameValue(AccountMapperTemplate.PASSWORD_EXPIRY_DATE_PARAMETER_NAME)
        ).where(new CommonCriteria.QueryByIdCriteria(AccountMapperTemplate.ACCOUNT_ID_PARAMETER_NAME)).build();
    }


    public String changeAccountExpiryDate(
            ProviderContext context,
            @Param(AccountMapperTemplate.ACCOUNT_ID_PARAMETER_NAME) String accountId,
            @Param(AccountMapperTemplate.ACCOUNT_EXPIRY_DATE_PARAMETER_NAME) @Nullable Date accountExpiryDate
    ) {
        Class<?> accountType = getDomainObjectType(context);
        return StatementBuilderStaticAccessor.builder()
                .update(accountType).column(
                        FieldScannerStaticAccessor.scanner().column(accountType, AccountTraits.AccountExpiryDate.class),
                        column -> new ParameterNameValue(AccountMapperTemplate.ACCOUNT_EXPIRY_DATE_PARAMETER_NAME)
                ).where(new CommonCriteria.QueryByIdCriteria(AccountMapperTemplate.ACCOUNT_ID_PARAMETER_NAME)).build();
    }

    public String changeAccountMobilePhoneNumber(
            ProviderContext context,
            @Param(AccountMapperTemplate.ACCOUNT_ID_PARAMETER_NAME) String accountId,
            @Param(AccountMapperTemplate.MOBILE_PHONE_NUMBER_PARAMETER_NAME) @Nullable String mobilePhoneNumber
    ) {
        Class<?> accountType = getDomainObjectType(context);
        return StatementBuilderStaticAccessor.builder()
                .update(accountType).column(
                        FieldScannerStaticAccessor.scanner().column(accountType, ContactTraits.MobilePhoneNumber.class),
                        column -> new ParameterNameValue(AccountMapperTemplate.MOBILE_PHONE_NUMBER_PARAMETER_NAME)
                ).where(new CommonCriteria.QueryByIdCriteria(AccountMapperTemplate.ACCOUNT_ID_PARAMETER_NAME)).build();
    }

    public String changeAccountEmail(
            ProviderContext context,
            @Param(AccountMapperTemplate.ACCOUNT_ID_PARAMETER_NAME) String accountId,
            @Param(AccountMapperTemplate.EMAIL_PARAMETER_NAME) @Nullable String email
    ) {
        Class<?> accountType = getDomainObjectType(context);
        return StatementBuilderStaticAccessor.builder()
                .update(accountType).column(
                        FieldScannerStaticAccessor.scanner().column(accountType, ContactTraits.Email.class),
                        column -> new ParameterNameValue(AccountMapperTemplate.EMAIL_PARAMETER_NAME)
                ).where(new CommonCriteria.QueryByIdCriteria(AccountMapperTemplate.ACCOUNT_ID_PARAMETER_NAME)).build();
    }

    public String updateAccount(ProviderContext context, @Param(AccountMapperTemplate.ACCOUNT_ENTITY_PARAMETER_NAME) A account) {
        Class<?> accountType = getDomainObjectType(context);
        return StatementBuilderStaticAccessor.builder()
                .update(
                        accountType,
                        new CommonScannerCallbacks.UpdateStatementIgnoredAnnotations(),
                        AccountMapperTemplate.ACCOUNT_ENTITY_PARAMETER_NAME
                ).where(new CommonCriteria.QueryByIdCriteria(
                        AccountMapperTemplate.ACCOUNT_ENTITY_PARAMETER_NAME,
                        FieldScannerStaticAccessor.scanner().field(accountType, GenericTraits.Id.class)
                )).build();
    }

    @SuppressWarnings("unchecked")
    public String searchAccount(
            ProviderContext context,
            @Param(AccountMapperTemplate.ACCOUNT_CRITERIA_PARAMETER_NAME) C criteria,
            @Nullable Collection<Class<?>> must,
            @Nullable Collection<Class<?>> should,
            boolean showPassword,
            long pageNo,
            long pageSize
    ) {
        return this.createSearchAccountStatementInternally(
                (Class<? extends AccountMapperTemplate<?, ?>>) context.getMapperType(),
                criteria,
                AccountMapperTemplate.ACCOUNT_CRITERIA_PARAMETER_NAME,
                must,
                should,
                showPassword,
                pageNo,
                pageSize
        ).countFoundRows(true).build();
    }

    private SelectStatement createSearchAccountStatementInternally(
            Class<? extends AccountMapperTemplate<?, ?>> mapperType,
            C criteria, String criteriaParameterName,
            @Nullable Collection<Class<?>> must,
            @Nullable Collection<Class<?>> should,
            boolean showPassword,
            long pageNo,
            long pageSize
    ) {
        Class<?> domainObjectType = getDomainObjectType(mapperType);
        //创建一个查询所有的语句
        SelectStatement statement = StatementBuilderStaticAccessor.builder().select(
                domainObjectType,
                new CommonScannerCallbacks.CompositeCallbacks().include(
                        new CommonScannerCallbacks.SelectStatementIgnoredAnnotations()
                ).exclude(
                        //决策是否返回密码
                        !showPassword ? new CommonScannerCallbacks.UsingTraits(
                                Arrays.asList(
                                        AccountTraits.Credentials.class,
                                        AccountTraits.CredentialsExpiryDate.class
                                )
                        ) : (fieldScanner, aClass) -> Collections.emptyList()
                )
        );
        MatchAllCriteria queryCriteria = buildQueryCriteria(
                domainObjectType, criteria, criteriaParameterName,
                must,
                should
        );
        if (!queryCriteria.isEmpty()) {
            statement.where(queryCriteria);
        }
        PaginationUtils.appendPagination(statement, pageNo, pageSize);
        return statement;
    }

    /**
     * 创建查询条件
     *
     * @param criteria 条件
     * @return 由查询条件对应的trait以及查询语句构成的对照关系
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
    public SelectStatement createSearchAccountStatement(
            Class<? extends AccountMapperTemplate<?, ?>> mapperType,
            C criteria, String criteriaParameterName,
            @Nullable Collection<Class<?>> must,
            @Nullable Collection<Class<?>> should) {
        return createSearchAccountStatementInternally(mapperType, criteria, criteriaParameterName, must, should, false, 1, Long.MAX_VALUE);
    }
}
