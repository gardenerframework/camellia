package com.jdcloud.gardener.camellia.uac.account.atomic;

import com.jdcloud.gardener.camellia.uac.account.atomic.verifier.AccountMustExistVerifier;
import com.jdcloud.gardener.camellia.uac.account.atomic.verifier.UniqueAccountFoundVerifier;
import com.jdcloud.gardener.camellia.uac.account.atomic.verifier.ZeroAccountFoundVerifier;
import com.jdcloud.gardener.camellia.uac.account.dao.mapper.AccountMapperTemplate;
import com.jdcloud.gardener.camellia.uac.account.schema.criteria.AccountCriteriaTemplate;
import com.jdcloud.gardener.camellia.uac.account.schema.entity.AccountEntityTemplate;
import com.jdcloud.gardener.camellia.uac.common.atomic.PasswordEncoder;
import com.jdcloud.gardener.camellia.uac.common.atomic.verifier.PasswordVerifier;
import com.jdcloud.gardener.fragrans.data.practice.operation.CommonOperations;
import com.jdcloud.gardener.fragrans.data.practice.operation.checker.RecordChecker;
import com.jdcloud.gardener.fragrans.data.practice.operation.checker.RecordCollectionChecker;
import com.jdcloud.gardener.fragrans.data.schema.query.GenericQueryResult;
import com.jdcloud.gardener.fragrans.data.trait.contact.ContactTraits;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author zhanghan30
 * @date 2022/9/16 2:40 下午
 */
@RequiredArgsConstructor
public class AccountAtomicOperationTemplate<A extends AccountEntityTemplate, C extends AccountCriteriaTemplate> {
    /**
     * 加载当前激活的激活的mapper
     */
    @Getter(AccessLevel.PROTECTED)
    @Setter(value = AccessLevel.PRIVATE, onMethod = @__({@Autowired}))
    private AccountMapperTemplate<A, C> accountMapper;
    /**
     * 这个非常关键  用来决定使用什么机制
     * <p>
     * 开发人员需要实现这个接口来指明要创建的账户应当怎么找到和识别相同的账户
     * <p>
     * 默认是找用户名、人脸、手机号、微信号、支付宝号
     * <p>
     * 按用户名
     * <p>
     * 默认加载当前激活的类型
     */
    @Getter(AccessLevel.PROTECTED)
    @Setter(value = AccessLevel.PRIVATE, onMethod = @__({@Autowired}))
    private PrincipalQueryCriteriaFactory<A, C> principalQueryCriteriaFactory;
    /**
     * 常见的操作的bean
     */
    @Getter(AccessLevel.PROTECTED)
    @Setter(value = AccessLevel.PRIVATE, onMethod = @__({@Autowired}))
    private CommonOperations commonOperations;

    /**
     * 创建账户，需要明确指出编码器
     * <p>
     * 要求创建的类型与密码编码器的类型要匹配和兼容(使用的事A的超类的类型)
     *
     * @param account         账户
     * @param passwordEncoder 密码编码器
     */
    public void createAccount(@NonNull A account, @NonNull PasswordEncoder<? super A> passwordEncoder) {
        //编码密码
        account.setPassword(passwordEncoder.encode(account, account.getPassword()));
        concatName(account);
        //按账号查找账户
        commonOperations.readThenCheck().collection(
                () -> accountMapper.searchAccount(
                        //这里A是否和激活的类型匹配，需要开发自己去处理
                        principalQueryCriteriaFactory.createQueryCriteriaByAccount(account),
                        null,
                        principalQueryCriteriaFactory.getPrincipalFieldTraits(),
                        false,
                        1, Long.MAX_VALUE
                ),
                ZeroAccountFoundVerifier.builder().build()
        );
        //创建账户
        accountMapper.createAccount(Objects.requireNonNull(account));
    }

    /**
     * 读取账户
     *
     * @param accountId    账户id
     * @param showPassword 是否显示密码
     * @param verifiers    要执行的检查清单
     * @return 账户信息
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public A readAccount(@NonNull String accountId, boolean showPassword, RecordChecker<? super A>... verifiers) {
        return commonOperations.readThenCheck().single(() -> (A) accountMapper.readAccount(Objects.requireNonNull(accountId), showPassword), verifiers);
    }

    /**
     * 安全读取账户，如果不存在则直接抛出异常而不需要进行判断
     *
     * @param accountId    账户id
     * @param showPassword 是否显示密码
     * @param verifiers    其它验证器
     * @return 账户
     */
    @SuppressWarnings("unchecked")
    public A safeReadAccount(@NonNull String accountId, boolean showPassword, RecordChecker<? super A>... verifiers) {
        List<RecordChecker<? super A>> allVerifiers = new LinkedList<>();
        if (verifiers != null) {
            allVerifiers.addAll(Arrays.asList(verifiers));
        }
        allVerifiers.add(0, AccountMustExistVerifier.builder()
                .recordIds(Collections.singletonList(accountId))
                .build()
        );
        RecordChecker<? super A>[] array = allVerifiers.toArray(new RecordChecker[]{});
        return readAccount(accountId, showPassword, array);
    }

    /**
     * 更改锁定状态
     *
     * @param accountId 账户id
     * @param status    状态
     * @return 之前的状态
     */
    @SuppressWarnings("unchecked")
    public boolean changeAccountLockStatus(@NonNull String accountId, boolean status) {
        A account = safeReadAccount(accountId, false);
        accountMapper.changeAccountLockStatus(accountId, status);
        return account.isLocked();
    }

    /**
     * 变更账户锁定状态
     *
     * @param accountId 账户id
     * @param status    状态
     * @return 之前的状态
     */
    @SuppressWarnings("unchecked")
    public boolean changeAccountEnableStatus(@NonNull String accountId, boolean status) {
        A account = safeReadAccount(accountId, false);
        accountMapper.changeAccountEnableStatus(accountId, status);
        return account.isEnabled();
    }

    /**
     * 修改密码
     *
     * @param accountId          账户id
     * @param password           密码
     * @param passwordEncoder    密码编码器
     * @param passwordExpiryDate 密码过期时间
     */
    @SuppressWarnings("unchecked")
    public void changePassword(@NonNull String accountId, String password, @NonNull PasswordEncoder<? super A> passwordEncoder, @Nullable Date passwordExpiryDate) {
        A account = safeReadAccount(accountId, false);
        accountMapper.changePassword(accountId, passwordEncoder.encode(account, password), passwordExpiryDate);
    }

    /**
     * 修改账户过期时间
     *
     * @param accountId         账户id
     * @param accountExpiryDate 账户过期时间
     * @return 之前的过期时间
     */
    @SuppressWarnings({"unchecked"})
    @Nullable
    public Date changeAccountExpiryDate(
            @NonNull String accountId,
            @Nullable Date accountExpiryDate
    ) {
        A account = safeReadAccount(accountId, false);
        accountMapper.changeAccountExpiryDate(accountId, accountExpiryDate);
        return account.getAccountExpiryDate();
    }

    /**
     * 修改手机号
     *
     * @param accountId         账户id
     * @param mobilePhoneNumber 手机号
     * @return 之前的手机号
     */
    @Nullable
    public String changeAccountMobilePhoneNumber(
            @NonNull String accountId,
            @Nullable String mobilePhoneNumber
    ) {
        Collection<Class<?>> principalFieldTraits = principalQueryCriteriaFactory.getPrincipalFieldTraits();
        if (!CollectionUtils.isEmpty(principalFieldTraits)
                &&
                principalFieldTraits.contains(ContactTraits.MobilePhoneNumber.class)
                && StringUtils.hasText(mobilePhoneNumber)) {
            //查看手机号是否已经存在
            commonOperations.readThenCheck().collection(
                    () -> accountMapper.searchAccount(
                            principalQueryCriteriaFactory.createQueryCriteriaByAccountByMobilePhoneNumber(
                                    mobilePhoneNumber
                            ),
                            Collections.singletonList(ContactTraits.MobilePhoneNumber.class),
                            null,
                            false,
                            1,
                            Long.MAX_VALUE
                    ),
                    ZeroAccountFoundVerifier.builder().build()
            );
        }
        A account = safeReadAccount(accountId, false);
        accountMapper.changeAccountMobilePhoneNumber(accountId, mobilePhoneNumber);
        return account.getMobilePhoneNumber();
    }

    /**
     * 修改邮箱
     *
     * @param accountId 账户id
     * @param email     手机号
     * @return 之前的手机号
     */
    @Nullable
    public String changeAccountEmail(
            @NonNull String accountId,
            @Nullable String email
    ) {
        Collection<Class<?>> principalFieldTraits = principalQueryCriteriaFactory.getPrincipalFieldTraits();
        if (!CollectionUtils.isEmpty(principalFieldTraits)
                &&
                principalFieldTraits.contains(ContactTraits.Email.class)
                && StringUtils.hasText(email)) {
            //查看邮箱是否已经存在
            commonOperations.readThenCheck().collection(
                    () -> accountMapper.searchAccount(
                            principalQueryCriteriaFactory.createQueryCriteriaByAccountByEmail(
                                    email
                            ),
                            Collections.singletonList(ContactTraits.Email.class),
                            null,
                            false,
                            1,
                            Long.MAX_VALUE
                    ),
                    ZeroAccountFoundVerifier.builder().build()
            );
        }
        A account = safeReadAccount(accountId, false);
        accountMapper.changeAccountEmail(accountId, email);
        return account.getEmail();
    }

    /**
     * 更新账户
     *
     * @param account 账户定西
     */
    @SuppressWarnings({"unchecked"})
    public A updateAccount(@NonNull A account) {
        A before = safeReadAccount(account.getId(), false);
        //确保擦除密码
        before.setPassword(null);
        //设置名字
        concatName(account);
        accountMapper.updateAccount(account);
        return before;
    }

    /**
     * 搜索账户
     *
     * @param criteria 搜索条件
     * @param pageNo   页码
     * @param pageSize 页面大小
     * @return 搜索结果
     */
    public GenericQueryResult<A> searchAccount(
            @NonNull C criteria,
            Collection<Class<?>> must,
            Collection<Class<?>> should,
            long pageNo,
            long pageSize
    ) {
        return new GenericQueryResult<>(
                //结果集
                accountMapper.searchAccount(
                        criteria,
                        must,
                        should,
                        false,
                        pageNo,
                        pageSize
                ),
                //总条数
                commonOperations.getFoundRows()
        );
    }

    /**
     * 使用账户id认证账户
     *
     * @param accountId       账户id
     * @param password        密码
     * @param passwordEncoder 密码编码器
     * @return 认证完成的账户
     */
    public A authenticate(
            @NonNull String accountId,
            @NonNull String password,
            @NonNull PasswordEncoder<? super A> passwordEncoder
    ) {
        return safeReadAccount(
                accountId,
                true,
                PasswordVerifier.<A>builder().password(password).passwordEncoder(passwordEncoder).build()
        );
    }

    /**
     * 进行账户搜索以及认证
     *
     * @param criteria        账户搜索条件 - 实际填写账号相关条件即可
     * @param password        密码
     * @param passwordEncoder 密码编码器
     */
    public A authenticate(
            @NonNull C criteria,
            @NonNull String password,
            @NonNull PasswordEncoder<? super A> passwordEncoder
    ) {
        Collection<A> accounts = commonOperations.readThenCheck().collection(
                () -> accountMapper.searchAccount(criteria, null, principalQueryCriteriaFactory.getPrincipalFieldTraits(), true, 1, Long.MAX_VALUE),
                UniqueAccountFoundVerifier.builder().build(),
                new RecordCollectionChecker<A>() {
                    @Override
                    public <T extends A> void check(Collection<T> records) {
                        Assert.isTrue(records.size() == 1, "UniqueAccountFoundVerifier seems not work");
                        records.forEach(
                                record -> PasswordVerifier.<A>builder().password(password).passwordEncoder(passwordEncoder).build().check((A) record)
                        );
                    }
                }
        );
        A account = Objects.requireNonNull(accounts).stream().findFirst().get();
        //移除密码
        account.setPassword(null);
        return account;
    }

    /**
     * 把名字连在一起
     *
     * @param account 账户
     */
    private void concatName(@NonNull AccountEntityTemplate account) {
        if (StringUtils.hasText(account.getSurname()) || StringUtils.hasText(account.getGivenName())) {
            StringBuilder nameBuilder = new StringBuilder();
            if (StringUtils.hasText(account.getSurname())) {
                nameBuilder.append(account.getSurname());
            }
            if (StringUtils.hasText(account.getGivenName())) {
                nameBuilder.append(account.getGivenName());
            }
            account.setName(nameBuilder.toString());
        }
    }
}
