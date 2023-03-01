package com.jdcloud.gardener.camellia.uac.joint.operation;

import com.jdcloud.gardener.camellia.uac.application.atomic.ApplicationAtomicOperationTemplate;
import com.jdcloud.gardener.camellia.uac.application.schema.criteria.ApplicationCriteriaTemplate;
import com.jdcloud.gardener.camellia.uac.application.schema.entity.ApplicationEntityTemplate;
import com.jdcloud.gardener.camellia.uac.client.atomic.ClientAtomicOperationTemplate;
import com.jdcloud.gardener.camellia.uac.client.schema.criteria.ClientCriteriaTemplate;
import com.jdcloud.gardener.camellia.uac.client.schema.entity.ClientEntityTemplate;
import com.jdcloud.gardener.camellia.uac.joint.dao.mapper.ApplicationClientRelationMapper;
import com.jdcloud.gardener.camellia.uac.joint.schema.entity.ApplicationClientRelation;
import com.jdcloud.gardener.camellia.uac.joint.schema.entity.ApplicationClientRelationDetail;
import com.jdcloud.gardener.fragrans.data.practice.operation.CommonOperations;
import com.jdcloud.gardener.fragrans.data.schema.query.GenericQueryResult;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

/**
 * @author ZhangHan
 * @date 2022/11/24 17:59
 */
@RequiredArgsConstructor
public class ApplicationClientDataOperation {
    private final ApplicationClientRelationMapper relationMapper;
    @SuppressWarnings("rawtypes")
    private final ApplicationAtomicOperationTemplate applicationAtomicOperation;
    @SuppressWarnings("rawtypes")
    private final ClientAtomicOperationTemplate clientAtomicOperation;
    private final CommonOperations commonOperations;

    /**
     * 创建客户端以及关联关系
     *
     * @param relation 关系
     */
    @SuppressWarnings("unchecked")
    public void createRelation(
            @NonNull ApplicationClientRelation relation
    ) {
        applicationAtomicOperation.safeReadApplication(relation.getApplicationId());
        clientAtomicOperation.safeReadClient(relation.getClientId(), false);
        relationMapper.createRelation(relation);
    }

    /**
     * 搜索关系
     *
     * @param applicationCriteria 应用搜索条件
     * @param applicationMust     应用必须满足的trait class
     * @param applicationShould   应用可选满足的trait class
     * @param clientCriteria      客户端搜索条件
     * @param clientMust          客户端必须满足的trait class
     * @param clientShould        客户端可选满足的trait class
     * @param pageNo              页码
     * @param pageSize            页大小
     * @param <A>                 应用类型
     * @param <C>                 客户端类型
     * @return 搜索结果
     */
    public <A extends ApplicationEntityTemplate, C extends ClientEntityTemplate> GenericQueryResult<ApplicationClientRelationDetail<A, C>> searchRelation(
            ApplicationCriteriaTemplate applicationCriteria,
            Collection<Class<?>> applicationMust,
            Collection<Class<?>> applicationShould,
            ClientCriteriaTemplate clientCriteria,
            Collection<Class<?>> clientMust,
            Collection<Class<?>> clientShould,
            long pageNo,
            long pageSize
    ) {
        return new GenericQueryResult<>(
                relationMapper.searchRelation(
                        applicationCriteria, applicationMust, applicationShould,
                        clientCriteria, clientMust, clientShould,
                        pageNo, pageSize
                ),
                commonOperations.getFoundRows()
        );
    }
}
