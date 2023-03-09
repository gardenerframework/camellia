package com.jdcloud.gardener.camellia.uac.joint.schema.entity;

import com.jdcloud.gardener.camellia.uac.application.schema.trait.ApplicationRelation;
import com.jdcloud.gardener.camellia.uac.client.schema.trait.ClientRelation;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.annotation.TableName;
import com.jdcloud.gardener.fragrans.data.schema.relation.BasicOperationTraceableRelation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author ZhangHan
 * @date 2022/11/24 17:06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("application_client_relation")
public class ApplicationClientRelation extends BasicOperationTraceableRelation implements
        ApplicationRelation,
        ClientRelation {
    /**
     * 应用id
     */
    private String applicationId;
    /**
     * 客户端id
     */
    private String clientId;


    public ApplicationClientRelation(Date createdTime, Date lastUpdateTime, String creator, String updater, String applicationId, String clientId) {
        super(createdTime, lastUpdateTime, creator, updater);
        this.applicationId = applicationId;
        this.clientId = clientId;
    }


}
