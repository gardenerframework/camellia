package com.jdcloud.gardener.camellia.uac.account.service;

import com.jdcloud.gardener.camellia.uac.account.atomic.AccountAtomicOperationTemplate;
import com.jdcloud.gardener.camellia.uac.account.configuration.AccountSecurityOption;
import com.jdcloud.gardener.camellia.uac.account.event.schema.*;
import com.jdcloud.gardener.camellia.uac.account.schema.criteria.AccountCriteriaTemplate;
import com.jdcloud.gardener.camellia.uac.account.schema.entity.AccountEntityTemplate;
import com.jdcloud.gardener.camellia.uac.account.schema.request.*;
import com.jdcloud.gardener.camellia.uac.account.schema.response.AccountAppearanceTemplate;
import com.jdcloud.gardener.camellia.uac.account.schema.response.CreateAccountResponse;
import com.jdcloud.gardener.camellia.uac.account.skeleton.AccountSkeletons;
import com.jdcloud.gardener.camellia.uac.common.atomic.PasswordEncoder;
import com.jdcloud.gardener.camellia.uac.common.schema.criteria.DomainCriteriaWrapper;
import com.jdcloud.gardener.camellia.uac.common.schema.request.PaginationParameter;
import com.jdcloud.gardener.camellia.uac.common.schema.request.SecurityOperationParameter;
import com.jdcloud.gardener.camellia.uac.common.schema.response.SearchResponse;
import com.jdcloud.gardener.camellia.uac.common.security.PasswordGenerator;
import com.jdcloud.gardener.camellia.uac.common.utils.GenericTypeUtils;
import com.jdcloud.gardener.fragrans.api.security.schema.Operator;
import com.jdcloud.gardener.fragrans.data.persistence.orm.entity.FieldScanner;
import com.jdcloud.gardener.fragrans.data.practice.log.schema.details.EntityFieldDetails;
import com.jdcloud.gardener.fragrans.data.practice.log.schema.details.EntityFieldValueDetails;
import com.jdcloud.gardener.fragrans.data.practice.log.schema.details.EntityIdDetails;
import com.jdcloud.gardener.fragrans.data.schema.query.GenericQueryResult;
import com.jdcloud.gardener.fragrans.data.trait.account.AccountTraits;
import com.jdcloud.gardener.fragrans.data.trait.contact.ContactTraits;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import com.jdcloud.gardener.fragrans.data.unique.HostIdGenerator;
import com.jdcloud.gardener.fragrans.data.unique.UniqueIdGenerator;
import com.jdcloud.gardener.fragrans.log.GenericOperationLogger;
import com.jdcloud.gardener.fragrans.log.common.schema.state.Done;
import com.jdcloud.gardener.fragrans.log.common.schema.verb.Create;
import com.jdcloud.gardener.fragrans.log.common.schema.verb.Update;
import com.jdcloud.gardener.fragrans.log.schema.content.GenericOperationLogContent;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.convert.converter.Converter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zhanghan30
 * @date 2022/9/16 3:04 下午
 */
@RequiredArgsConstructor
@Slf4j
public abstract class AccountServiceTemplate<
        C extends CreateAccountParameterTemplate,
        S extends SearchAccountCriteriaParameterTemplate,
        U extends UpdateAccountParameterTemplate,
        P extends AuthenticateAccountParameterTemplate,
        A extends AccountAppearanceTemplate,
        T extends AccountEntityTemplate,
        I extends AccountCriteriaTemplate
        > implements
        AccountSkeletons.MethodPrototype.CreateAccount<C>,
        AccountSkeletons.MethodPrototype.ReadAccount<A>,
        AccountSkeletons.MethodPrototype.LockAccount,
        AccountSkeletons.MethodPrototype.UnlockAccount,
        AccountSkeletons.MethodPrototype.EnableAccount,
        AccountSkeletons.MethodPrototype.DisableAccount,
        AccountSkeletons.MethodPrototype.Authenticate<P, A>,
        AccountSkeletons.MethodPrototype.ChangeAccountExpiryDate,
        AccountSkeletons.MethodPrototype.ChangeEmail,
        AccountSkeletons.MethodPrototype.ChangeMobilePhoneNumber,
        AccountSkeletons.MethodPrototype.ChangePassword,
        AccountSkeletons.MethodPrototype.SearchAccount<S, A>,
        AccountSkeletons.MethodPrototype.UpdateAccount<U>,
        InitializingBean,
        ApplicationEventPublisherAware {
    private final Converter<T, A> accountPoToVoConverter;
    private final Converter<S, DomainCriteriaWrapper<I>> searchAccountCriteriaParameterToCriteriaConverter;
    private final Converter<C, T> createAccountParameterToPoConverter;
    private final Converter<U, T> updateAccountParameterToPoConverter;
    private final Converter<P, I> authenticateAccountParameterToCriteriaConverter;
    @Getter(AccessLevel.PROTECTED)
    private final FieldScanner fieldScanner = new FieldScanner();
    @Getter(AccessLevel.PROTECTED)
    private final GenericOperationLogger operationLogger = new GenericOperationLogger();
    @Getter(AccessLevel.PROTECTED)
    @Setter(value = AccessLevel.PRIVATE, onMethod = @__({@Autowired}))
    private HostIdGenerator hostIdGenerator;
    /**
     * 获取操作者详情
     */
    @Getter(AccessLevel.PROTECTED)
    @Setter(value = AccessLevel.PRIVATE, onMethod = @__({@Autowired}))
    private Operator operator;
    @Getter(AccessLevel.PROTECTED)
    @Setter(value = AccessLevel.PRIVATE, onMethod = @__({@Autowired}))
    private AccountAtomicOperationTemplate<T, I> accountAtomicOperation;
    @Getter(AccessLevel.PROTECTED)
    @Setter(value = AccessLevel.PRIVATE, onMethod = @__({@Autowired}))
    private PasswordGenerator<? super T> passwordGenerator;
    @Getter(AccessLevel.PROTECTED)
    @Setter(value = AccessLevel.PRIVATE, onMethod = @__({@Autowired}))
    private PasswordEncoder<? super T> passwordEncoder;
    @Getter(AccessLevel.PROTECTED)
    @Setter(value = AccessLevel.PRIVATE, onMethod = @__({@Autowired}))
    private AccountSecurityOption securityOption;
    private UniqueIdGenerator accountIdGenerator;
    private ApplicationEventPublisher eventPublisher;

    /**
     * 创建账户
     *
     * @param parameter 创建账户参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreateAccountResponse createAccount(@NonNull C parameter) {
        //分配账户id
        String accountId = accountIdGenerator.nextId("A");
        T account = Objects.requireNonNull(createAccountParameterToPoConverter.convert(parameter));
        boolean passwordGenerated = false;
        String generatedPassword = null;
        if (!StringUtils.hasText(account.getPassword())
                &&
                AccountSecurityOption
                        .EmptyPasswordStrategy.GENERATE
                        .equals(securityOption.getEmptyPasswordStrategy())
        ) {
            generatedPassword = passwordGenerator.generate(account);
            Assert.hasText(generatedPassword, String.format("no password generated: \"%s\"", generatedPassword));
            passwordGenerated = true;
            account.setPassword(generatedPassword);
        }
        //将同样的id写入账户中
        account.setId(accountId);
        account.setPasswordExpiryDate(
                securityOption.getPasswordValidityPeriod() == null ?
                        null :
                        DateUtils.addDays(new Date(), securityOption.getPasswordValidityPeriod())
        );
        //写入创建人
        account.setCreator(operator.getUserId());
        //创建账户
        accountAtomicOperation.createAccount(account, passwordEncoder);
        //准备发布事件，删除账户密码
        account.setPassword(null);
        operationLogger.info(
                log,
                GenericOperationLogContent.builder()
                        .what(AccountEntityTemplate.class)
                        .operation(new Create())
                        .state(new Done())
                        .detail(new EntityIdDetails<>(accountId))
                        .build(),
                null
        );
        //发布事件
        eventPublisher.publishEvent(new AccountCreatedEvent(
                account,
                passwordGenerated,
                generatedPassword
        ));
        return new CreateAccountResponse(accountId);
    }

    /**
     * 读取账户
     *
     * @param accountId 账户id
     * @return 账户VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("unchecked")
    public A readAccount(@NonNull @Valid @NotBlank String accountId) {
        //读取账户
        T account = accountAtomicOperation.safeReadAccount(accountId, false);
        //再次确定删除密码
        account.setPassword(null);
        //转成响应内容
        return Objects.requireNonNull(accountPoToVoConverter.convert(GenericTypeUtils.cast(account)));
    }

    /**
     * 搜索账户
     *
     * @param searchAccountCriteriaParameter 搜索参数
     * @param paginationParameter            分页参数
     * @return 搜索结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SearchResponse<A> searchAccount(
            @NonNull @Valid S searchAccountCriteriaParameter,
            @NonNull @Valid PaginationParameter paginationParameter
    ) {
        DomainCriteriaWrapper<I> criteriaWrapper = searchAccountCriteriaParameterToCriteriaConverter.convert(searchAccountCriteriaParameter);
        GenericQueryResult<T> accountEntityTemplateGenericQueryResult = accountAtomicOperation.searchAccount(
                Objects.requireNonNull(criteriaWrapper).getCriteria(),
                criteriaWrapper.getMust(),
                criteriaWrapper.getShould(),
                paginationParameter.getPageNo(),
                paginationParameter.getPageSize()
        );
        return SearchResponse.<A>builder()
                .total(accountEntityTemplateGenericQueryResult.getTotal())
                .contents(accountEntityTemplateGenericQueryResult.getContents().stream().map(
                        accountEntityTemplate -> accountPoToVoConverter.convert(GenericTypeUtils.cast(accountEntityTemplate))
                ).collect(Collectors.toList()))
                .build();
    }

    /**
     * 锁定账户
     *
     * @param accountId 账户id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void lockAccount(@NonNull @Valid @NotBlank String accountId,
                            @Valid SecurityOperationParameter securityOperationParameter) {
        changeAccountLockStatus(accountId, true);
    }

    /**
     * 解锁账户
     *
     * @param accountId 账户id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlockAccount(@NonNull @Valid @NotBlank String accountId,
                              @Valid SecurityOperationParameter securityOperationParameter) {
        changeAccountLockStatus(accountId, false);
    }

    /**
     * 完成锁定任务执行
     *
     * @param accountId 账户id
     * @param status    状态
     */
    private void changeAccountLockStatus(@NonNull String accountId, boolean status) {
        boolean before = accountAtomicOperation.changeAccountLockStatus(accountId, status);
        if (before == status) {
            //没变化，跳过后面的逻辑
            return;
        }
        operationLogger.info(
                log,
                GenericOperationLogContent.builder()
                        .what(AccountEntityTemplate.class)
                        .operation(new Update())
                        .state(new Done())
                        .detail(new EntityFieldValueDetails<>(
                                accountId,
                                fieldScanner.field(AccountEntityTemplate.class, GenericTraits.StatusTraits.LockFlag.class),
                                status
                        ))
                        .build(),
                null
        );
        eventPublisher.publishEvent(new AccountStatusChangedEvent(
                accountId,
                before,
                status,
                fieldScanner
                        .field(AccountEntityTemplate.class, GenericTraits.StatusTraits.LockFlag.class)
        ));
    }

    /**
     * 激活账户
     *
     * @param accountId 账户id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableAccount(@NonNull @Valid @NotBlank String accountId,
                              @Valid SecurityOperationParameter securityOperationParameter) {
        changeAccountEnableStatus(accountId, true);
    }

    /**
     * 禁用账户
     *
     * @param accountId 账户id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableAccount(@NonNull @Valid @NotBlank String accountId,
                               @Valid SecurityOperationParameter securityOperationParameter) {
        changeAccountEnableStatus(accountId, false);
    }

    /**
     * 完成锁定任务执行
     *
     * @param accountId 账户id
     * @param status    状态
     */
    private void changeAccountEnableStatus(@NonNull String accountId, boolean status) {
        boolean before = accountAtomicOperation.changeAccountEnableStatus(accountId, status);
        if (before == status) {
            //没变化，跳过后面的逻辑
            return;
        }
        operationLogger.info(
                log,
                GenericOperationLogContent.builder()
                        .what(AccountEntityTemplate.class)
                        .operation(new Update())
                        .state(new Done())
                        .detail(new EntityFieldValueDetails<>(
                                accountId,
                                fieldScanner.field(AccountEntityTemplate.class, GenericTraits.StatusTraits.EnableFlag.class),
                                status
                        ))
                        .build(),
                null
        );
        eventPublisher.publishEvent(new AccountStatusChangedEvent(
                accountId,
                before,
                status,
                fieldScanner.field(AccountEntityTemplate.class, GenericTraits.StatusTraits.EnableFlag.class)
        ));
    }

    /**
     * 修改密码
     *
     * @param accountId               账户id
     * @param changePasswordParameter 参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(@NonNull @Valid @NotBlank String accountId, @NonNull @Valid ChangePasswordParameter changePasswordParameter) {
        String originalPassword = changePasswordParameter.getOriginalPassword();
        if (StringUtils.hasText(originalPassword)) {
            //执行认证
            accountAtomicOperation.authenticate(accountId, originalPassword, passwordEncoder);
        }
        String generatedPassword = changePasswordParameter.getPassword();
        boolean passwordGenerated = false;
        //决策是否要生成密码
        if (!StringUtils.hasText(generatedPassword)
                &&
                AccountSecurityOption
                        .EmptyPasswordStrategy.GENERATE
                        .equals(securityOption.getEmptyPasswordStrategy())
        ) {
            generatedPassword = passwordGenerator.generate(accountAtomicOperation.readAccount(accountId, false));
            Assert.hasText(generatedPassword, String.format("no password generated: \"%s\"", generatedPassword));
            passwordGenerated = true;
            changePasswordParameter.setPassword(generatedPassword);
        }
        accountAtomicOperation.changePassword(
                accountId,
                changePasswordParameter.getPassword(),
                passwordEncoder,
                securityOption.getPasswordValidityPeriod() == null ?
                        null :
                        DateUtils.addDays(new Date(), securityOption.getPasswordValidityPeriod())
        );
        operationLogger.info(
                log,
                GenericOperationLogContent.builder()
                        .what(AccountEntityTemplate.class)
                        .operation(new Update())
                        .state(new Done())
                        .detail(new EntityFieldDetails<>(accountId,
                                fieldScanner.field(
                                        AccountEntityTemplate.class, AccountTraits.Credentials.class
                                )))
                        .build(),
                null
        );
        eventPublisher.publishEvent(new AccountPasswordChangedEvent(
                accountId,
                passwordGenerated,
                passwordGenerated ? generatedPassword : null
        ));
    }

    /**
     * 修改手机号
     *
     * @param accountId                        账户id
     * @param changeMobilePhoneNumberParameter 参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeMobilePhoneNumber(@NonNull @Valid @NotBlank String accountId, @NonNull @Valid ChangeMobilePhoneNumberParameter changeMobilePhoneNumberParameter) {
        String after = changeMobilePhoneNumberParameter.getMobilePhoneNumber();
        String before = accountAtomicOperation.changeAccountMobilePhoneNumber(accountId, after);
        if (!Objects.equals(before, after)) {
            operationLogger.info(
                    log,
                    GenericOperationLogContent.builder()
                            .what(AccountEntityTemplate.class)
                            .operation(new Update())
                            .state(new Done())
                            .detail(new EntityFieldDetails<>(accountId,
                                    fieldScanner.field(
                                            AccountEntityTemplate.class, ContactTraits.MobilePhoneNumber.class
                                    )))
                            .build(),
                    null
            );
            //是否要发布事件
        }
    }

    /**
     * 修改邮箱
     *
     * @param accountId            账户id
     * @param changeEmailParameter 参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeEmail(@NonNull @Valid @NotBlank String accountId, @NonNull @Valid ChangeEmailParameter changeEmailParameter) {
        String after = changeEmailParameter.getEmail();
        String before = accountAtomicOperation.changeAccountEmail(accountId, after);
        if (!Objects.equals(before, after)) {
            operationLogger.info(
                    log,
                    GenericOperationLogContent.builder()
                            .what(AccountEntityTemplate.class)
                            .operation(new Update())
                            .state(new Done())
                            .detail(new EntityFieldDetails<>(accountId,
                                    fieldScanner.field(
                                            AccountEntityTemplate.class, ContactTraits.Email.class
                                    )))
                            .build(),
                    null
            );
            //是否要发布事件
        }
    }

    /**
     * 修改账户过期时间
     *
     * @param accountId                        账户id
     * @param changeAccountExpiryDateParameter 参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeAccountExpiryDate(@NonNull @Valid @NotBlank String accountId, @NonNull @Valid ChangeAccountExpiryDateParameter changeAccountExpiryDateParameter) {
        Date before = accountAtomicOperation.changeAccountExpiryDate(accountId, changeAccountExpiryDateParameter.getAccountExpiryDate());
        if (before == changeAccountExpiryDateParameter.getAccountExpiryDate()) {
            return;
        }
        operationLogger.info(
                log,
                GenericOperationLogContent.builder()
                        .what(AccountEntityTemplate.class)
                        .operation(new Update())
                        .state(new Done())
                        .detail(new EntityFieldDetails<>(accountId, fieldScanner.field(
                                AccountEntityTemplate.class, AccountTraits.AccountExpiryDate.class
                        ))).build(),
                null
        );
        eventPublisher.publishEvent(new AccountExpiryDateChangedEvent(
                accountId,
                before,
                changeAccountExpiryDateParameter.getAccountExpiryDate(),
                fieldScanner.field(AccountEntityTemplate.class, AccountTraits.AccountExpiryDate.class)
        ));
    }

    /**
     * 更新账户
     *
     * @param accountId              账户id
     * @param updateAccountParameter 更新参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("unchecked")
    public void updateAccount(@NonNull @Valid @NotBlank String accountId, @NonNull @Valid U updateAccountParameter) {
        T account = Objects.requireNonNull(
                updateAccountParameterToPoConverter
                        .convert(updateAccountParameter)
        );
        //设置账户id
        account.setId(accountId);
        //设置操作人id
        account.setUpdater(operator.getUserId());
        T before = accountAtomicOperation.updateAccount(account);
        operationLogger.info(
                log,
                GenericOperationLogContent.builder()
                        .what(AccountEntityTemplate.class)
                        .operation(new Update())
                        .state(new Done())
                        .detail(new EntityIdDetails<>(accountId)).build(),
                null
        );
        //确定清楚密码
        before.setPassword(null);
        eventPublisher.publishEvent(new AccountChangedEvent(
                before,
                //这里严格去数据库中重新查一次
                accountAtomicOperation.safeReadAccount(accountId, false)
        ));
    }

    /**
     * 认证账户
     *
     * @param parameter 认证请求
     * @return 账户VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public A authenticate(@NonNull @Valid P parameter) {
        return accountPoToVoConverter.convert(accountAtomicOperation.authenticate(
                Objects.requireNonNull(authenticateAccountParameterToCriteriaConverter.convert(parameter)),
                parameter.getPassword(),
                passwordEncoder
        ));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.accountIdGenerator = new UniqueIdGenerator(hostIdGenerator.getHostId());
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }

}
