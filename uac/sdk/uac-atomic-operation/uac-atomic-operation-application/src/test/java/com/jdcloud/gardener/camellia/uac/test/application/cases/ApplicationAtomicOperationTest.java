package com.jdcloud.gardener.camellia.uac.test.application.cases;

import com.jdcloud.gardener.camellia.uac.application.atomic.ApplicationAtomicOperationTemplate;
import com.jdcloud.gardener.camellia.uac.application.defaults.schema.criteria.DefaultApplicationCriteria;
import com.jdcloud.gardener.camellia.uac.application.defaults.schema.entity.DefaultApplicationEntity;
import com.jdcloud.gardener.camellia.uac.application.exception.client.ApplicationNotFoundException;
import com.jdcloud.gardener.camellia.uac.application.schema.entity.ApplicationEntityTemplate;
import com.jdcloud.gardener.camellia.uac.test.UacAtomicOperationTestApplication;
import com.jdcloud.gardener.fragrans.data.schema.entity.BasicEntity;
import com.jdcloud.gardener.fragrans.data.schema.query.GenericQueryResult;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author zhanghan30
 * @date 2022/11/9 13:18
 */
@SpringBootTest(classes = UacAtomicOperationTestApplication.class)
public class ApplicationAtomicOperationTest {
    @Autowired
    private ApplicationAtomicOperationTemplate<DefaultApplicationEntity, DefaultApplicationCriteria> applicationAtomicOperation;

    @Test
    public void smokeTest() {
        DefaultApplicationEntity application = DefaultApplicationEntity.builder()
                .id(UUID.randomUUID().toString())
                .name(UUID.randomUUID().toString())
                .build();
        applicationAtomicOperation.createApplication(application);
        //搜索
        GenericQueryResult<DefaultApplicationEntity> applicationEntityTemplateGenericQueryResult = applicationAtomicOperation.searchApplication(
                DefaultApplicationCriteria.builder().name(application.getName()).build(),
                Collections.singletonList(GenericTraits.Name.class),
                null,
                1,
                100
        );
        Assertions.assertEquals(1, applicationEntityTemplateGenericQueryResult.getContents().size());
        Assertions.assertTrue(
                applicationEntityTemplateGenericQueryResult.getContents().stream().map(
                        BasicEntity::getId
                ).collect(Collectors.toList()).contains(application.getId())
        );
        //更新
        application.setLogo(UUID.randomUUID().toString());
        applicationAtomicOperation.updateApplication(application);
        DefaultApplicationEntity applicationUpdated = applicationAtomicOperation.safeReadApplication(application.getId());
        Assertions.assertNotNull(applicationUpdated);
        Assertions.assertEquals(applicationUpdated.getLogo(), application.getLogo());
        //读取一个不存在的
        Assertions.assertThrows(
                ApplicationNotFoundException.class,
                () -> applicationAtomicOperation.safeReadApplication(UUID.randomUUID().toString())
        );
        //变更激活状态
        applicationAtomicOperation.changeApplicationEnableStatus(application.getId(), true);
        Assertions.assertTrue(applicationAtomicOperation.safeReadApplication(application.getId()).isEnabled());
        applicationAtomicOperation.changeApplicationEnableStatus(application.getId(), false);
        Assertions.assertFalse(applicationAtomicOperation.safeReadApplication(application.getId()).isEnabled());
    }
}
