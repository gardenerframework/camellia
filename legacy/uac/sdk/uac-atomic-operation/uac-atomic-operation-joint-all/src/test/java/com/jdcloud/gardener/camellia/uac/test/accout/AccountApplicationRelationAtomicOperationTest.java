package com.jdcloud.gardener.camellia.uac.test.accout;

import com.jdcloud.gardener.camellia.uac.account.atomic.AccountAtomicOperation;
import com.jdcloud.gardener.camellia.uac.account.atomic.PasswordEncoder;
import com.jdcloud.gardener.camellia.uac.account.defaults.schema.criteria.DefaultAccountCriteria;
import com.jdcloud.gardener.camellia.uac.account.defaults.schema.entity.DefaultAccountEntity;
import com.jdcloud.gardener.camellia.uac.application.atomic.ApplicationAtomicOperation;
import com.jdcloud.gardener.camellia.uac.application.atomic.verifer.ApplicationMustBeEnabledVerifier;
import com.jdcloud.gardener.camellia.uac.application.defaults.schema.criteria.DefaultApplicationCriteria;
import com.jdcloud.gardener.camellia.uac.application.defaults.schema.entity.DefaultApplicationEntity;
import com.jdcloud.gardener.camellia.uac.application.exception.client.ApplicationDisabledException;
import com.jdcloud.gardener.camellia.uac.common.schema.criteria.DomainCriteriaWrapper;
import com.jdcloud.gardener.camellia.uac.joint.atomic.AccountApplicationRelationAtomicOperation;
import com.jdcloud.gardener.camellia.uac.joint.schema.aggregation.AccountApplicationAggregation;
import com.jdcloud.gardener.camellia.uac.joint.schema.entity.AccountApplicationRelation;
import com.jdcloud.gardener.camellia.uac.test.UacAtomicOperationTestApplication;
import com.jdcloud.gardener.fragrans.data.schema.query.trait.QueryResult;
import com.jdcloud.gardener.fragrans.data.trait.account.AccountTraits;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2022/11/7 17:01
 */
@SpringBootTest(classes = UacAtomicOperationTestApplication.class)
public class AccountApplicationRelationAtomicOperationTest {
    @Autowired
    //这里已经用了子类的mapper
    private AccountApplicationRelationAtomicOperation atomicOperation;
    @Autowired
    private AccountAtomicOperation accountAtomicOperation;
    @Autowired
    private ApplicationAtomicOperation applicationAtomicOperation;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void smokeTest() {
        DefaultAccountEntity account = DefaultAccountEntity.builder()
                .id(UUID.randomUUID().toString())
                .password(UUID.randomUUID().toString())
                .username(UUID.randomUUID().toString())
                .build();
        DefaultApplicationEntity application = DefaultApplicationEntity.builder().
                id(UUID.randomUUID().toString())
                .name(UUID.randomUUID().toString())
                .build();
        accountAtomicOperation.createAccount(account, passwordEncoder);
        applicationAtomicOperation.createApplication(application);
        //搜索
        QueryResult<AccountApplicationAggregation<DefaultAccountEntity, DefaultApplicationEntity>> accountApplicationAggregationQueryResult = atomicOperation.aggregationSearch(
                new DomainCriteriaWrapper<>(DefaultAccountCriteria.builder().username(account.getUsername()).build(),
                        Collections.singletonList(AccountTraits.Username.class), null),
                new DomainCriteriaWrapper<>(DefaultApplicationCriteria.builder().name(application.getName()).build(),
                        Collections.singletonList(GenericTraits.Name.class), null),
                1, 100
        );
        Assertions.assertEquals(0, accountApplicationAggregationQueryResult.getTotal());
        Assertions.assertThrows(
                ApplicationDisabledException.class,
                () -> atomicOperation.createRelation(AccountApplicationRelation.builder().applicationId(
                        application.getId()
                ).accountId(account.getId()).build())
        );
        application.setEnabled(true);
        atomicOperation.createRelation(AccountApplicationRelation.builder().applicationId(
                        application.getId()
                ).accountId(account.getId()).build(),
                Collections.singletonList(
                        ApplicationMustBeEnabledVerifier.class
                ));
        accountApplicationAggregationQueryResult = atomicOperation.aggregationSearch(
                new DomainCriteriaWrapper<>(DefaultAccountCriteria.builder().username(account.getUsername()).build(),
                        Collections.singletonList(AccountTraits.Username.class), null),
                new DomainCriteriaWrapper<>(DefaultApplicationCriteria.builder().name(application.getName()).build(),
                        Collections.singletonList(GenericTraits.Name.class), null),
                1, 100
        );
        Assertions.assertEquals(1, accountApplicationAggregationQueryResult.getTotal());
        application.setId(UUID.randomUUID().toString());
        application.setLogo(UUID.randomUUID().toString());
        applicationAtomicOperation.createApplication(application);
        accountApplicationAggregationQueryResult = atomicOperation.aggregationSearch(
                new DomainCriteriaWrapper<>(DefaultAccountCriteria.builder().username(account.getUsername()).build(),
                        Collections.singletonList(AccountTraits.Username.class), null),
                new DomainCriteriaWrapper<>(DefaultApplicationCriteria.builder().name(application.getName()).build(),
                        Collections.singletonList(GenericTraits.Name.class), null),
                1, 100
        );
        //添加应用但没有关系不影响搜索结果
        Assertions.assertEquals(1, accountApplicationAggregationQueryResult.getTotal());
        atomicOperation.createRelation(
                AccountApplicationRelation.builder().accountId(account.getId()).applicationId(application.getId()).build()
        );
        accountApplicationAggregationQueryResult = atomicOperation.aggregationSearch(
                new DomainCriteriaWrapper<>(DefaultAccountCriteria.builder().username(account.getUsername()).build(),
                        Collections.singletonList(AccountTraits.Username.class), null),
                new DomainCriteriaWrapper<>(DefaultApplicationCriteria.builder().name(application.getName()).build(),
                        Collections.singletonList(GenericTraits.Name.class), null),
                1, 100
        );
        //添加应用但没有关系不影响搜索结果
        Assertions.assertEquals(2, accountApplicationAggregationQueryResult.getTotal());
    }
}
