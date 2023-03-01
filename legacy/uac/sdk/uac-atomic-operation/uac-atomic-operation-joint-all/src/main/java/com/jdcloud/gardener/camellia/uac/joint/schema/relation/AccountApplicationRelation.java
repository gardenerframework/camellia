package com.jdcloud.gardener.camellia.uac.joint.schema.entity;

import com.jdcloud.gardener.camellia.uac.application.schema.trait.ApplicationRelation;
import com.jdcloud.gardener.camellia.uac.joint.schema.trait.JointTraits;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.annotation.TableName;
import com.jdcloud.gardener.fragrans.data.schema.annotation.ImmutableField;
import com.jdcloud.gardener.fragrans.data.schema.annotation.UpdateBySpecificOperation;
import com.jdcloud.gardener.fragrans.data.schema.relation.BasicRelation;
import com.jdcloud.gardener.fragrans.data.trait.account.AccountTraits;
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
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@TableName("account_application_relation")
@LogTarget("账户与应用关系")
@SuperBuilder
public class AccountApplicationRelation extends BasicRelation implements
        AccountTraits.AccountRelation<String>,
        ApplicationRelation,
        JointTraits.AccountApplicationRelationExpiryDate {
    /**
     * 账户id
     */
    @ImmutableField
    private String accountId;
    /**
     * 应用id
     */
    @ImmutableField
    private String applicationId;
    /**
     * 关系的过期时间
     */
    @UpdateBySpecificOperation
    private Date accountApplicationRelationExpiryDate;
}

