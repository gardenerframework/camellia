package com.jdcloud.gardener.camellia.uac.joint.atomic;

import com.jdcloud.gardener.camellia.uac.account.atomic.AccountAtomicOperation;
import com.jdcloud.gardener.camellia.uac.account.schema.criteria.AccountCriteriaTemplate;
import com.jdcloud.gardener.camellia.uac.account.schema.entity.AccountEntityTemplate;
import com.jdcloud.gardener.camellia.uac.application.atomic.ApplicationAtomicOperation;
import com.jdcloud.gardener.camellia.uac.application.atomic.verifer.ApplicationMustBeEnabledVerifier;
import com.jdcloud.gardener.camellia.uac.application.schema.criteria.ApplicationCriteriaTemplate;
import com.jdcloud.gardener.camellia.uac.application.schema.entity.ApplicationEntityTemplate;
import com.jdcloud.gardener.camellia.uac.common.schema.criteria.DomainCriteriaWrapper;
import com.jdcloud.gardener.camellia.uac.joint.atomic.verifer.AccountApplicationRelationMustExistVerifier;
import com.jdcloud.gardener.camellia.uac.joint.dao.mapper.AccountApplicationRelationMapper;
import com.jdcloud.gardener.camellia.uac.joint.schema.aggregation.AccountApplicationAggregation;
import com.jdcloud.gardener.camellia.uac.joint.schema.entity.AccountApplicationRelation;
import com.jdcloud.gardener.fragrans.data.practice.operation.CommonOperations;
import com.jdcloud.gardener.fragrans.data.practice.operation.checker.RecordChecker;
import com.jdcloud.gardener.fragrans.data.schema.query.GenericQueryResult;
import com.jdcloud.gardener.fragrans.data.schema.query.trait.QueryResult;
import com.jdcloud.gardener.fragrans.log.GenericOperationLogger;
import com.jdcloud.gardener.fragrans.log.common.schema.state.Failed;
import com.jdcloud.gardener.fragrans.log.common.schema.verb.Create;
import com.jdcloud.gardener.fragrans.log.schema.content.GenericOperationLogContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.lang.Nullable;

import java.util.*;

/**
 * @author zhanghan30
 * @date 2022/11/7 11:52
 */
@RequiredArgsConstructor
@Slf4j
public class AccountApplicationRelationAtomicOperation {
    /**
     * mapper
     */
    private final AccountApplicationRelationMapper mapper;
    /**
     * 关系原子操作需要下级实体的原子操作
     * <p>
     * 关系被视作是实体的上级接口
     * <p>
     * 不要直接访问mapper，因为概念上，原子操作是对外暴露的最小单元
     */
    private final AccountAtomicOperation accountAtomicOperation;
    private final ApplicationAtomicOperation applicationAtomicOperation;
    private final GenericOperationLogger operationLogger;
    /**
     * 常用操作
     */
    private final CommonOperations commonOperations;

    /**
     * 创建关系
     * <p>
     * 应用如果没有激活则不允许创建
     *
     * @param relation 关系
     */
    public void createRelation(
            AccountApplicationRelation relation
    ) {
        createRelation(relation, null);
    }

    /**
     * 创建关系
     *
     * @param relation                 关系
     * @param omitApplicationVerifiers 忽略的应用验证器，
     *                                 但是验证应用是否存在的验证器无法被胡咧。
     *                                 目前已经激活的验证器有:
     *                                 <ul>
     *                                 <li>{@link ApplicationMustBeEnabledVerifier}</li>
     *                                 </ul>
     */
    public void createRelation(
            AccountApplicationRelation relation,
            @Nullable Collection<Class<? extends RecordChecker<? extends ApplicationEntityTemplate>>> omitApplicationVerifiers
    ) {
        if (omitApplicationVerifiers == null) {
            omitApplicationVerifiers = Collections.emptyList();
        }
        Collection<RecordChecker<? extends ApplicationEntityTemplate>>
                applicationVerifiers = new LinkedList<>();
        if (!omitApplicationVerifiers.contains(
                ApplicationMustBeEnabledVerifier.class
        )) {
            applicationVerifiers.add(
                    ApplicationMustBeEnabledVerifier
                            .builder()
                            .recordId(relation.getApplicationId())
                            .build()
            );
        }
        //检查账户和应用是否存在
        accountAtomicOperation.safeReadAccount(relation.getAccountId(), false);
        applicationAtomicOperation.safeReadApplication(relation.getApplicationId(),
                applicationVerifiers.toArray(new RecordChecker[]{}));
        //创建关系
        try {
            mapper.createRelation(relation);
        } catch (DuplicateKeyException e) {
            //关系重复
            operationLogger.debug(
                    log,
                    GenericOperationLogContent.builder().
                            what(AccountApplicationRelation.class).
                            state(new Failed()).operation(new Create()).build(),
                    e
            );
        }
    }

    /**
     * 读取关系
     *
     * @param accountId     账户id
     * @param applicationId 应用id
     * @param checkers      检查器
     * @return 关系
     */
    @Nullable
    public AccountApplicationRelation readRelation(String accountId, String applicationId, RecordChecker<AccountApplicationRelation>... checkers) {
        return commonOperations.readThenCheck().single(
                () -> mapper.readRelation(accountId, applicationId),
                checkers
        );
    }

    /**
     * 读取关系，如果不存在则报错
     *
     * @param accountId     账户id
     * @param applicationId 应用id
     * @param checkers      检查器
     * @return 关系
     */
    @SuppressWarnings("unchecked")
    public AccountApplicationRelation safeReadRelation(String accountId, String applicationId, RecordChecker<AccountApplicationRelation>... checkers) {
        Collection<RecordChecker<AccountApplicationRelation>> checkerList = new LinkedList<>();
        checkerList.add(new AccountApplicationRelationMustExistVerifier());
        if (checkers != null && checkers.length > 0) {
            checkerList.addAll(Arrays.asList(checkers));
        }
        return readRelation(accountId, applicationId, checkerList.toArray(new RecordChecker[]{}));
    }

    /**
     * 更改过去时间
     *
     * @param accountId          账户id
     * @param applicationId      应用id
     * @param relationExpiryDate 过期时间
     */
    public void changeExpiryDate(String accountId, String applicationId, @Nullable Date relationExpiryDate) {
        //确定关系存在
        safeReadRelation(accountId, applicationId);
        //改变日期
        mapper.changeExpiryDate(accountId, applicationId, relationExpiryDate);
    }

    /**
     * 聚合搜索
     *
     * @param accountCriteria     账户搜索条件
     * @param applicationCriteria 应用搜索条件
     * @param pageNo              页码
     * @param pageSize            页大小
     * @param <A>                 需要的账户类型(理应是当前激活的类型，或是其父类)
     * @param <P>                 需要的应用类型(理应是当前激活的类型，或是其父类)
     * @return 聚合查询结果
     */
    public <A extends AccountEntityTemplate, P extends ApplicationEntityTemplate>
    QueryResult<AccountApplicationAggregation<A, P>> aggregationSearch(
            DomainCriteriaWrapper<? extends AccountCriteriaTemplate> accountCriteria,
            DomainCriteriaWrapper<? extends ApplicationCriteriaTemplate> applicationCriteria,
            long pageNo,
            long pageSize
    ) {
        return new GenericQueryResult<>(
                mapper.aggregationSearch(
                        accountCriteria, applicationCriteria, pageNo, pageSize
                ),
                commonOperations.getFoundRows()
        );
    }
}
