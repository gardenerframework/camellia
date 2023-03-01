package com.jdcloud.gardener.camellia.uac.joint.dao.mapping;

import com.jdcloud.gardener.camellia.uac.application.dao.mapper.ApplicationMapperTemplate;
import com.jdcloud.gardener.camellia.uac.client.dao.mapper.ClientMapperTemplate;
import com.jdcloud.gardener.camellia.uac.joint.schema.entity.ApplicationClientRelationDetail;
import com.jdcloud.gardener.camellia.uac.joint.schema.trait.ApplicationClientJointTraits;

/**
 * @author ZhangHan
 * @date 2022/11/24 19:59
 */
public interface ApplicationClientDetailColumnMappings {
    class ApplicationColumnMapping extends JointColumnMappingBase {

        public ApplicationColumnMapping() {
            super(ApplicationMapperTemplate.class, ApplicationClientRelationDetail.class, ApplicationClientJointTraits.Application.class);
        }
    }

    class ClientColumnMapping extends JointColumnMappingBase {

        public ClientColumnMapping() {
            super(ClientMapperTemplate.class, ApplicationClientRelationDetail.class, ApplicationClientJointTraits.Client.class);
        }
    }
}
