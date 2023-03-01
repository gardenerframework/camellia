package com.jdcloud.gardener.camellia.uac.test.accout;

import com.jdcloud.gardener.camellia.uac.account.atomic.AccountAtomicOperationTemplate;
import com.jdcloud.gardener.camellia.uac.account.exception.client.AccountNotFoundException;
import com.jdcloud.gardener.camellia.uac.account.exception.client.AccountPropertyUniqueConstraintsViolationException;
import com.jdcloud.gardener.camellia.uac.common.atomic.PasswordEncoder;
import com.jdcloud.gardener.camellia.uac.common.exception.client.IncorrectPasswordException;
import com.jdcloud.gardener.camellia.uac.test.UacAtomicOperationTestApplication;
import com.jdcloud.gardener.camellia.uac.test.accout.extend.ExtendAccount;
import com.jdcloud.gardener.camellia.uac.test.accout.extend.ExtendCriteria;
import com.jdcloud.gardener.camellia.uac.test.accout.extend.TestTrait;
import com.jdcloud.gardener.fragrans.data.schema.entity.BasicEntity;
import com.jdcloud.gardener.fragrans.data.schema.query.GenericQueryResult;
import com.jdcloud.gardener.fragrans.data.trait.account.AccountTraits;
import com.jdcloud.gardener.fragrans.data.trait.contact.ContactTraits;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author zhanghan30
 * @date 2022/9/16 11:55 下午
 */
@SpringBootTest(classes = UacAtomicOperationTestApplication.class)
public class AccountAtomicOperationTemplateTest {
    @Autowired
    //这里已经用了子类的mapper
    private AccountAtomicOperationTemplate<ExtendAccount, ExtendCriteria> atomicOperation;
    @Autowired
    private PasswordEncoder<? super ExtendAccount> passwordEncoder;

    @Test
    @DisplayName("冒烟测试")
    public void smokeTest() {
        String password = UUID.randomUUID().toString();
        ExtendAccount account = ExtendAccount.builder().
                id(UUID.randomUUID().toString())
                .username(UUID.randomUUID().toString())
                .mobilePhoneNumber(UUID.randomUUID().toString())
                .email(UUID.randomUUID().toString())
                .weChatOpenId(UUID.randomUUID().toString())
                .alipayOpenId(UUID.randomUUID().toString())
                .password(password)
                .test(UUID.randomUUID().toString())
                .surname(UUID.randomUUID().toString())
                .build();
        atomicOperation.createAccount(account, passwordEncoder);
        //重复创建会出错
        Assertions.assertThrows(AccountPropertyUniqueConstraintsViolationException.class,
                () -> atomicOperation.createAccount(account, passwordEncoder)
        );
        //按id查询
        GenericQueryResult<ExtendAccount> accountEntityTemplateGenericQueryResult = atomicOperation.searchAccount(ExtendCriteria.builder().id(account.getId()).build(), Collections.singletonList(GenericTraits.Id.class), null, 1, 100);
        Assertions.assertEquals(1, accountEntityTemplateGenericQueryResult.getContents().size());
        Assertions.assertTrue(accountEntityTemplateGenericQueryResult.getContents().stream().map(BasicEntity::getId).collect(Collectors.toSet()).contains(account.getId()));
        //按用户名查询
        accountEntityTemplateGenericQueryResult = atomicOperation.searchAccount(ExtendCriteria.builder().username(account.getUsername()).build(), Collections.singletonList(AccountTraits.Username.class), null, 1, 100);
        Assertions.assertEquals(1, accountEntityTemplateGenericQueryResult.getContents().size());
        Assertions.assertTrue(accountEntityTemplateGenericQueryResult.getContents().stream().map(BasicEntity::getId).collect(Collectors.toSet()).contains(account.getId()));
        //按手机号查询
        accountEntityTemplateGenericQueryResult = atomicOperation.searchAccount(ExtendCriteria.builder().mobilePhoneNumber(account.getMobilePhoneNumber()).build(), Collections.singletonList(ContactTraits.MobilePhoneNumber.class), null, 1, 100);
        Assertions.assertEquals(1, accountEntityTemplateGenericQueryResult.getContents().size());
        Assertions.assertTrue(accountEntityTemplateGenericQueryResult.getContents().stream().map(BasicEntity::getId).collect(Collectors.toSet()).contains(account.getId()));
        //按用户名和手机号并且查询
        accountEntityTemplateGenericQueryResult = atomicOperation.searchAccount(ExtendCriteria.builder().mobilePhoneNumber(account.getMobilePhoneNumber()).username(account.getUsername()).build(), Arrays.asList(AccountTraits.Username.class, ContactTraits.MobilePhoneNumber.class), null, 1, 100);
        Assertions.assertEquals(1, accountEntityTemplateGenericQueryResult.getContents().size());
        Assertions.assertTrue(accountEntityTemplateGenericQueryResult.getContents().stream().map(BasicEntity::getId).collect(Collectors.toSet()).contains(account.getId()));
        //按名查询
        accountEntityTemplateGenericQueryResult = atomicOperation.searchAccount(ExtendCriteria.builder().name(account.getSurname()).build(), Arrays.asList(GenericTraits.Name.class), null, 1, 100);
        Assertions.assertEquals(1, accountEntityTemplateGenericQueryResult.getContents().size());
        Assertions.assertTrue(accountEntityTemplateGenericQueryResult.getContents().stream().map(BasicEntity::getId).collect(Collectors.toSet()).contains(account.getId()));
        //激活和取消激活
        atomicOperation.changeAccountEnableStatus(account.getId(), true);
        ExtendAccount extendAccount = atomicOperation.readAccount(account.getId(), false);
        Assertions.assertNotNull(extendAccount);
        Assertions.assertTrue(extendAccount.isEnabled());
        atomicOperation.changeAccountEnableStatus(account.getId(), false);
        extendAccount = atomicOperation.readAccount(account.getId(), false);
        Assertions.assertNotNull(extendAccount);
        Assertions.assertFalse(extendAccount.isEnabled());
        //锁定和取消锁定
        atomicOperation.changeAccountLockStatus(account.getId(), true);
        extendAccount = atomicOperation.readAccount(account.getId(), false);
        Assertions.assertNotNull(extendAccount);
        Assertions.assertTrue(extendAccount.isLocked());
        atomicOperation.changeAccountLockStatus(account.getId(), false);
        extendAccount = atomicOperation.readAccount(account.getId(), false);
        Assertions.assertNotNull(extendAccount);
        Assertions.assertFalse(extendAccount.isLocked());
        //更新账户
        account.setTest(UUID.randomUUID().toString());
        atomicOperation.updateAccount(account);
        extendAccount = atomicOperation.readAccount(account.getId(), false);
        Assertions.assertNotNull(extendAccount);
        Assertions.assertEquals(account.getTest(), extendAccount.getTest());
        atomicOperation.authenticate(
                ExtendCriteria.builder().username(account.getUsername()).build(),
                password,
                passwordEncoder);
        atomicOperation.authenticate(
                account.getId(),
                password,
                passwordEncoder
        );
        Assertions.assertThrows(
                IncorrectPasswordException.class,
                () -> atomicOperation.<ExtendAccount, ExtendCriteria>authenticate(ExtendCriteria.builder().username(account.getUsername()).build(), UUID.randomUUID().toString(), passwordEncoder)
        );
        //读取一个不存在的账户
        Assertions.assertThrows(
                AccountNotFoundException.class,
                () -> atomicOperation.safeReadAccount(UUID.randomUUID().toString(), false)
        );
        //更新手机号
        //重复手机号无法更新
        Assertions.assertThrowsExactly(
                AccountPropertyUniqueConstraintsViolationException.class,
                () -> atomicOperation.changeAccountMobilePhoneNumber(account.getId(), account.getMobilePhoneNumber())
        );
        //换一个手机号
        account.setMobilePhoneNumber(UUID.randomUUID().toString());
        atomicOperation.changeAccountMobilePhoneNumber(account.getId(), account.getMobilePhoneNumber());
        extendAccount = atomicOperation.readAccount(account.getId(), false);
        Assertions.assertEquals(account.getMobilePhoneNumber(), extendAccount.getMobilePhoneNumber());
        //更换邮箱
        Assertions.assertThrowsExactly(
                AccountPropertyUniqueConstraintsViolationException.class,
                () -> atomicOperation.changeAccountEmail(account.getId(), account.getEmail())
        );
        //换一个邮箱
        account.setEmail(UUID.randomUUID().toString());
        atomicOperation.changeAccountEmail(account.getId(), account.getEmail());
        extendAccount = atomicOperation.readAccount(account.getId(), false);
        Assertions.assertEquals(account.getEmail(), extendAccount.getEmail());
        //执行批量
        atomicOperation.searchAccount(
                ExtendCriteria.builder().ids(Arrays.asList(account.getId(), account.getId())).test(UUID.randomUUID().toString()).build(),
                Arrays.asList(GenericTraits.Ids.class, TestTrait.class),
                null,
                1, 100
        );
    }
}
