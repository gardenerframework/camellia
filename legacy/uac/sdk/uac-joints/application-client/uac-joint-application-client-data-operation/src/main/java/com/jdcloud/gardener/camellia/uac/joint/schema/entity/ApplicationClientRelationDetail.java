package com.jdcloud.gardener.camellia.uac.joint.schema.entity;

import com.jdcloud.gardener.camellia.uac.application.schema.entity.ApplicationEntityTemplate;
import com.jdcloud.gardener.camellia.uac.client.schema.entity.ClientEntityTemplate;
import com.jdcloud.gardener.camellia.uac.joint.schema.trait.ApplicationClientJointTraits;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author ZhangHan
 * @date 2022/11/24 17:15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ApplicationClientRelationDetail<A extends ApplicationEntityTemplate, C extends ClientEntityTemplate>
        extends ApplicationClientRelation implements ApplicationClientJointTraits.Application<A>, ApplicationClientJointTraits.Client<C> {
    /**
     * 应用程序
     */
    private A application;
    /**
     * 客户端
     */
    private C client;

    public ApplicationClientRelationDetail(Date createdTime, Date lastUpdateTime, String creator, String updater, String applicationId, String clientId, A application, C client) {
        super(createdTime, lastUpdateTime, creator, updater, applicationId, clientId);
        this.application = application;
        this.client = client;
    }
}
