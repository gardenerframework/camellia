package com.jdcloud.gardener.camellia.uac.joint.dao.mapper;

import com.jdcloud.gardener.camellia.uac.application.schema.criteria.ApplicationCriteriaTemplate;
import com.jdcloud.gardener.camellia.uac.application.schema.entity.ApplicationEntityTemplate;
import com.jdcloud.gardener.camellia.uac.client.schema.criteria.ClientCriteriaTemplate;
import com.jdcloud.gardener.camellia.uac.client.schema.entity.ClientEntityTemplate;
import com.jdcloud.gardener.camellia.uac.joint.dao.mapping.ApplicationClientDetailColumnMappings;
import com.jdcloud.gardener.camellia.uac.joint.dao.sql.ApplicationClientRelationSqlProvider;
import com.jdcloud.gardener.camellia.uac.joint.schema.entity.ApplicationClientRelation;
import com.jdcloud.gardener.camellia.uac.joint.schema.entity.ApplicationClientRelationDetail;
import com.jdcloud.gardener.fragrans.data.persistence.orm.mapping.annotation.ColumnTypeHandler;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.Collection;

/**
 * @author ZhangHan
 * @date 2022/11/24 17:11
 */
@Mapper
public interface ApplicationClientRelationMapper {
    String APPLICATION_CLIENT_RELATION_PARAMETER_NAME = "relation";
    String APPLICATION_CRITERIA_PARAMETER_NAME = "applicationCriteria";
    String CLIENT_CRITERIA_PARAMETER_NAME = "clientCriteria";

    /**
     * 创建关系
     *
     * @param relation 关系
     */
    @InsertProvider(ApplicationClientRelationSqlProvider.class)
    void createRelation(
            @Param(APPLICATION_CLIENT_RELATION_PARAMETER_NAME) ApplicationClientRelation relation
    );

    /**
     * 执行联合搜索
     *
     * @param applicationCriteria 应用程序搜索条件
     * @param applicationMust     应用搜索中的必备
     * @param applicationShould   应用搜索中的可选
     * @param clientCriteria      客户端搜索条就按
     * @param clientMust          客户端搜索的必须
     * @param clientShould        客户端搜索的可选
     * @param pageNo              页码
     * @param pageSize            页大小
     * @param <A>                 应用程序类型
     * @param <C>                 客户端类型
     * @return 搜索结果
     */
    @SelectProvider(ApplicationClientRelationSqlProvider.class)
    @ColumnTypeHandler(provider = {
            ApplicationClientDetailColumnMappings.ApplicationColumnMapping.class,
            ApplicationClientDetailColumnMappings.ClientColumnMapping.class

    })
    <A extends ApplicationEntityTemplate, C extends ClientEntityTemplate> Collection<ApplicationClientRelationDetail<A, C>> searchRelation(
            @Param(APPLICATION_CRITERIA_PARAMETER_NAME) ApplicationCriteriaTemplate applicationCriteria,
            Collection<Class<?>> applicationMust,
            Collection<Class<?>> applicationShould,
            @Param(CLIENT_CRITERIA_PARAMETER_NAME) ClientCriteriaTemplate clientCriteria,
            Collection<Class<?>> clientMust,
            Collection<Class<?>> clientShould,
            long pageNo,
            long pageSize
    );
}
