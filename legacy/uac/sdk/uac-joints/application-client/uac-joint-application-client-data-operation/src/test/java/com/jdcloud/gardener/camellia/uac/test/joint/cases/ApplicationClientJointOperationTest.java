package com.jdcloud.gardener.camellia.uac.test.joint.cases;

import com.jdcloud.gardener.camellia.uac.application.atomic.ApplicationAtomicOperationTemplate;
import com.jdcloud.gardener.camellia.uac.application.defaults.schema.criteria.DefaultApplicationCriteria;
import com.jdcloud.gardener.camellia.uac.application.defaults.schema.entity.DefaultApplicationEntity;
import com.jdcloud.gardener.camellia.uac.application.schema.entity.ApplicationEntityTemplate;
import com.jdcloud.gardener.camellia.uac.client.atomic.ClientAtomicOperationTemplate;
import com.jdcloud.gardener.camellia.uac.client.defaults.schema.criteria.DefaultClientCriteria;
import com.jdcloud.gardener.camellia.uac.client.defaults.schema.entity.DefaultClientEntity;
import com.jdcloud.gardener.camellia.uac.client.schema.entity.ClientEntityTemplate;
import com.jdcloud.gardener.camellia.uac.common.atomic.PasswordEncoder;
import com.jdcloud.gardener.camellia.uac.joint.operation.ApplicationClientDataOperation;
import com.jdcloud.gardener.camellia.uac.joint.schema.entity.ApplicationClientRelation;
import com.jdcloud.gardener.camellia.uac.joint.schema.entity.ApplicationClientRelationDetail;
import com.jdcloud.gardener.camellia.uac.test.UacJointOperationTestApplication;
import com.jdcloud.gardener.fragrans.data.schema.query.GenericQueryResult;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2022/11/9 13:18
 */
@SpringBootTest(classes = UacJointOperationTestApplication.class)
public class ApplicationClientJointOperationTest {
    @Autowired
    public PasswordEncoder<? super DefaultClientEntity> passwordEncoder;
    @Autowired
    private ApplicationClientDataOperation jointOperation;
    @Autowired
    private ApplicationAtomicOperationTemplate<DefaultApplicationEntity, DefaultApplicationCriteria> applicationCriteriaApplicationAtomicOperationTemplate;
    @Autowired
    private ClientAtomicOperationTemplate<DefaultClientEntity, DefaultClientCriteria> clientEntityDefaultClientCriteriaClientAtomicOperationTemplate;

    @Test
    public void smokeTest() {
        //先创建个应用
        DefaultApplicationEntity application = DefaultApplicationEntity.builder().id(
                UUID.randomUUID().toString()
        ).name(UUID.randomUUID().toString()).logo(UUID.randomUUID().toString()).build();
        applicationCriteriaApplicationAtomicOperationTemplate.createApplication(application);
        DefaultClientEntity client = DefaultClientEntity.builder().id(
                UUID.randomUUID().toString()
        ).name(UUID.randomUUID().toString()).password(UUID.randomUUID().toString()).grantType(Collections.singletonList("client_credentials")).build();
        clientEntityDefaultClientCriteriaClientAtomicOperationTemplate.createClient(client, passwordEncoder);
        //创建关系
        jointOperation.createRelation(new ApplicationClientRelation(application.getId(), client.getId()));
        //搜索一下
        GenericQueryResult<ApplicationClientRelationDetail<ApplicationEntityTemplate, ClientEntityTemplate>> queryResult = jointOperation.searchRelation(
                DefaultApplicationCriteria.builder().id(
                        application.getId()
                ).build(),
                Collections.singletonList(GenericTraits.Id.class),
                null,
                DefaultClientCriteria.builder().build(),
                null,
                null,
                1, 100
        );
        Assertions.assertEquals(1, queryResult.getContents().size());
        Optional<ApplicationClientRelationDetail<ApplicationEntityTemplate, ClientEntityTemplate>> first = queryResult.getContents().stream().findFirst();
        Assertions.assertEquals(application.getId(), first.get().getApplication().getId());
        Assertions.assertEquals(client.getId(), first.get().getClient().getId());
    }
}
