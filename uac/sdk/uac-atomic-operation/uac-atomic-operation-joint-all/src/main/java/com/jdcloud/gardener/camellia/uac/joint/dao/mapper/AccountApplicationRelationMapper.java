package com.jdcloud.gardener.camellia.uac.joint.dao.mapper;

import com.jdcloud.gardener.camellia.uac.account.schema.criteria.AccountCriteriaTemplate;
import com.jdcloud.gardener.camellia.uac.account.schema.entity.AccountEntityTemplate;
import com.jdcloud.gardener.camellia.uac.application.schema.criteria.ApplicationCriteriaTemplate;
import com.jdcloud.gardener.camellia.uac.application.schema.entity.ApplicationEntityTemplate;
import com.jdcloud.gardener.camellia.uac.common.schema.criteria.DomainCriteriaWrapper;
import com.jdcloud.gardener.camellia.uac.joint.dao.mapping.AggregationColumnMappings;
import com.jdcloud.gardener.camellia.uac.joint.dao.sql.AccountApplicationRelationSqlProvider;
import com.jdcloud.gardener.camellia.uac.joint.schema.aggregation.AccountApplicationAggregation;
import com.jdcloud.gardener.camellia.uac.joint.schema.entity.AccountApplicationRelation;
import com.jdcloud.gardener.fragrans.data.persistence.orm.mapping.annotation.ColumnTypeHandler;
import org.apache.ibatis.annotations.*;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Date;

/**
 * @author zhanghan30
 * @date 2022/11/7 10:46
 */
@Mapper
public interface AccountApplicationRelationMapper {
    String ACCOUNT_APPLICATION_RELATION_PARAMETER_NAME = "relation";
    String ACCOUNT_ID_PARAMETER_NAME = "accountId";
    String APPLICATION_ID_PARAMETER_NAME = "applicationId";
    String ACCOUNT_APPLICATION_RELATION_EXPIRY_DATE_PARAMETER_NAME = "expiryDate";
    String ACCOUNT_CRITERIA_PARAMETER_NAME = "accountCriteria";
    String APPLICATION_CRITERIA_PARAMETER_NAME = "applicationCriteria";


    /**
     * 创建关系
     *
     * @param relation 关系数据
     */
    @InsertProvider(AccountApplicationRelationSqlProvider.class)
    void createRelation(@Param(ACCOUNT_APPLICATION_RELATION_PARAMETER_NAME) AccountApplicationRelation relation);

    /**
     * 更新过期时间
     *
     * @param accountId                            账户id
     * @param applicationId                        应用id
     * @param accountApplicationRelationExpiryDate 过期时间，为空表达永不过期
     */
    @UpdateProvider(AccountApplicationRelationSqlProvider.class)
    void changeExpiryDate(@Param(ACCOUNT_ID_PARAMETER_NAME) String accountId, @Param(APPLICATION_ID_PARAMETER_NAME) String applicationId, @Param(ACCOUNT_APPLICATION_RELATION_EXPIRY_DATE_PARAMETER_NAME) Date accountApplicationRelationExpiryDate);

    /**
     * 读取关系数据
     *
     * @param accountId     账户id
     * @param applicationId 应用id
     * @return 关系
     */
    @Nullable
    @SelectProvider(AccountApplicationRelationSqlProvider.class)
    AccountApplicationRelation readRelation(@Param(ACCOUNT_ID_PARAMETER_NAME) String accountId, @Param(APPLICATION_ID_PARAMETER_NAME) String applicationId);

    /**
     * 聚合搜索
     *
     * @param accountCriteria     账户条件
     * @param applicationCriteria 账户条件
     * @param <A>                 需要的账户类型(警告: 这里其实必须是当前激活的类型或者是其父类)
     * @param <P>                 需要的应用类型(警告: 这里其实必须是当前激活的类型或者是其父类)
     * @return 搜索结果
     */
    @SelectProvider(AccountApplicationRelationSqlProvider.class)
    @ColumnTypeHandler(provider = {
            AggregationColumnMappings.AccountColumnMapping.class,
            AggregationColumnMappings.ApplicationColumnMapping.class
    })
    <A extends AccountEntityTemplate, P extends ApplicationEntityTemplate>
    Collection<AccountApplicationAggregation<A, P>> aggregationSearch(
            @Param(ACCOUNT_CRITERIA_PARAMETER_NAME) @Nullable DomainCriteriaWrapper<? extends AccountCriteriaTemplate> accountCriteria,
            @Param(APPLICATION_CRITERIA_PARAMETER_NAME) @Nullable DomainCriteriaWrapper<? extends ApplicationCriteriaTemplate> applicationCriteria,
            long pageNo,
            long pageSize
    );
}
