package com.jdcloud.gardener.camellia.uac.joint.schema.aggregation;

import com.jdcloud.gardener.camellia.uac.account.schema.entity.AccountEntityTemplate;
import com.jdcloud.gardener.camellia.uac.application.schema.entity.ApplicationEntityTemplate;
import com.jdcloud.gardener.camellia.uac.joint.schema.trait.JointTraits;
import com.jdcloud.gardener.fragrans.log.annotation.LogTarget;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;


/**
 * @author zhanghan30
 * @date 2022/11/7 10:31
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@LogTarget("账户与应用聚合")
@SuperBuilder
public class AccountApplicationAggregation<A extends AccountEntityTemplate, P extends ApplicationEntityTemplate>
        implements JointTraits.AggregationTraits.Account<A>, JointTraits.AggregationTraits.Application<P>,
        JointTraits.AccountApplicationRelationExpiryDate {
    /**
     * 包含的账户
     */
    private A account;
    /**
     * 包含的应用
     */
    private P application;
    /**
     * 关系过期时间
     */
    private Date accountApplicationRelationExpiryDate;
}

