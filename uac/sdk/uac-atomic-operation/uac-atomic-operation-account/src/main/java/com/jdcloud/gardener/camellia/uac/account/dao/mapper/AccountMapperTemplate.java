package com.jdcloud.gardener.camellia.uac.account.dao.mapper;

import com.jdcloud.gardener.camellia.uac.account.dao.sql.AccountSqlTemplate;
import com.jdcloud.gardener.camellia.uac.account.schema.criteria.AccountCriteriaTemplate;
import com.jdcloud.gardener.camellia.uac.account.schema.entity.AccountEntityTemplate;
import com.jdcloud.gardener.fragrans.data.persistence.template.annotation.DomainDaoTemplate;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Date;

/**
 * @author zhanghan30
 * @date 2022/9/20 12:51
 */
@DomainDaoTemplate
public interface AccountMapperTemplate<
        A extends AccountEntityTemplate,
        C extends AccountCriteriaTemplate
        > {
    String ACCOUNT_ENTITY_PARAMETER_NAME = "account";
    String ACCOUNT_ID_PARAMETER_NAME = "accountId";
    String PASSWORD_PARAMETER_NAME = "password";
    String PASSWORD_EXPIRY_DATE_PARAMETER_NAME = "passwordExpiryDate";
    String ACCOUNT_EXPIRY_DATE_PARAMETER_NAME = "accountExpiryDate";
    String MOBILE_PHONE_NUMBER_PARAMETER_NAME = "mobilePhoneNumber";
    String EMAIL_PARAMETER_NAME = "email";
    String ACCOUNT_CRITERIA_PARAMETER_NAME = "criteria";

    /**
     * 创建账户
     *
     * @param account 账户
     */
    @InsertProvider(AccountSqlTemplate.class)
    void createAccount(@Param(ACCOUNT_ENTITY_PARAMETER_NAME) A account);

    /**
     * 读取账户
     *
     * @param accountId    账户id
     * @param showPassword 是否给密码
     * @return 账户
     */
    @Nullable
    @SelectProvider(AccountSqlTemplate.class)
    A readAccount(@Param(ACCOUNT_ID_PARAMETER_NAME) String accountId, boolean showPassword);

    /**
     * 变更账户锁定状态
     *
     * @param accountId 账户id
     * @param status    状态
     */
    @UpdateProvider(AccountSqlTemplate.class)
    void changeAccountLockStatus(@Param(ACCOUNT_ID_PARAMETER_NAME) String accountId, boolean status);

    /**
     * 变更账户锁定状态
     *
     * @param accountId 账户id
     * @param status    状态
     */
    @UpdateProvider(AccountSqlTemplate.class)
    void changeAccountEnableStatus(@Param(ACCOUNT_ID_PARAMETER_NAME) String accountId, boolean status);

    /**
     * 修改密码
     *
     * @param accountId          账户id
     * @param password           密码
     * @param passwordExpiryDate 密码过期时间
     */
    @UpdateProvider(AccountSqlTemplate.class)
    void changePassword(
            @Param(ACCOUNT_ID_PARAMETER_NAME) String accountId,
            @Param(AccountMapperTemplate.PASSWORD_PARAMETER_NAME) String password,
            @Param(AccountMapperTemplate.PASSWORD_EXPIRY_DATE_PARAMETER_NAME) @Nullable Date passwordExpiryDate
    );

    /**
     * 修改账户过期时间
     *
     * @param accountId         账户id
     * @param accountExpiryDate 账户过期时间
     */
    @UpdateProvider(AccountSqlTemplate.class)
    void changeAccountExpiryDate(
            @Param(ACCOUNT_ID_PARAMETER_NAME) String accountId,
            @Param(ACCOUNT_EXPIRY_DATE_PARAMETER_NAME) @Nullable Date accountExpiryDate
    );

    /**
     * 更新手机号
     *
     * @param accountId         账户id
     * @param mobilePhoneNumber 手机号
     */
    @UpdateProvider(AccountSqlTemplate.class)
    void changeAccountMobilePhoneNumber(
            @Param(ACCOUNT_ID_PARAMETER_NAME) String accountId,
            @Param(MOBILE_PHONE_NUMBER_PARAMETER_NAME) @Nullable String mobilePhoneNumber
    );

    /**
     * 更新手机号
     *
     * @param accountId 账户id
     * @param email     邮箱
     */
    @UpdateProvider(AccountSqlTemplate.class)
    void changeAccountEmail(
            @Param(ACCOUNT_ID_PARAMETER_NAME) String accountId,
            @Param(EMAIL_PARAMETER_NAME) @Nullable String email
    );

    /**
     * 修改账户信息
     * <p>
     * 账户id必须包含其中
     *
     * @param account 账户信息
     */
    @UpdateProvider(AccountSqlTemplate.class)
    void updateAccount(
            @Param(ACCOUNT_ENTITY_PARAMETER_NAME) A account
    );

    /**
     * 查询账户
     * <p>
     * 当must 和 should都不为空的时候，逻辑是(must) and (should)
     *
     * @param criteria     账户查询条件
     * @param must         给定若干trait,这些trait表达的属性的查询是and的逻辑
     * @param should       给定若干trait,这些trait表达的属性的查询是or的逻辑
     * @param showPassword 是否显示密码(用于认证)
     * @param pageNo       页码
     * @param pageSize     页大小
     * @return 查询结果集合
     */
    @SelectProvider(AccountSqlTemplate.class)
    Collection<A> searchAccount(
            @Param(ACCOUNT_CRITERIA_PARAMETER_NAME) C criteria,
            @Nullable Collection<Class<?>> must,
            @Nullable Collection<Class<?>> should,
            boolean showPassword,
            long pageNo,
            long pageSize
    );
}
