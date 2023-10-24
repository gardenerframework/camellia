package io.gardenerframework.camellia.authorization.client.data.operation.test.cases;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gardenerframework.camellia.authorization.client.data.operation.test.ClientDataAtomicOperationTestApplication;
import io.gardenerframework.camellia.authorization.client.data.operation.test.bean.TestClientCriteria;
import io.gardenerframework.camellia.authorization.client.data.operation.test.bean.TestClientDataAtomicOperation;
import io.gardenerframework.camellia.authorization.client.data.operation.test.bean.TestClientEntity;
import io.gardenerframework.fragrans.data.schema.query.GenericQueryResult;
import io.gardenerframework.fragrans.data.trait.generic.GenericTraits;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

/**
 * @author chris
 * @date 2023/10/24
 */
@SpringBootTest(classes = ClientDataAtomicOperationTestApplication.class)
@MapperScan(basePackageClasses = ClientDataAtomicOperationTest.TruncateMapper.class)
@Slf4j
public class ClientDataAtomicOperationTest {
    @Autowired
    private TestClientDataAtomicOperation testClientDataAtomicOperation;

    @Autowired
    private TruncateMapper truncateMapper;

    @BeforeEach
    public void truncate() {
        truncateMapper.truncate();
    }

    @Test
    public void smokeTest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        TestClientEntity recordToSave = TestClientEntity.builder()
                .name(UUID.randomUUID().toString())
                .logo(UUID.randomUUID().toString())
                .description(UUID.randomUUID().toString())
                .grantType(Arrays.asList(
                        "client_credentials",
                        "refresh_token"
                ))
                .accessTokenTtl(60000)
                .refreshTokenTtl(60000)
                .password(UUID.randomUUID().toString())
                .scope(Collections.singleton("any"))
                .redirectUrl(null)
                .build();
        String clientId = testClientDataAtomicOperation.createClient(recordToSave);
        TestClientEntity saved = testClientDataAtomicOperation.readClient(clientId, true);
        Assertions.assertNotNull(saved);
        GenericQueryResult<TestClientEntity> queryResult = testClientDataAtomicOperation.searchClient(
                TestClientCriteria.builder().build(),
                Collections.singleton(GenericTraits.LiteralTraits.Name.class),
                null,
                1,
                10
        );
        Assertions.assertNotNull(queryResult);
        Assertions.assertFalse(CollectionUtils.isEmpty(queryResult.getContents()));
        Assertions.assertEquals(1, queryResult.getTotal());
        //测试一下性能
        for(int i = 0; i < 10000; i++) {
            recordToSave.setName(UUID.randomUUID().toString());
            testClientDataAtomicOperation.createClient(recordToSave);
            log.info(String.valueOf(i));
        }
        log.info(Instant.now().toString());
        testClientDataAtomicOperation.searchClient(
                TestClientCriteria.builder().name(recordToSave.getName()).build(),
                Collections.singleton(GenericTraits.LiteralTraits.Name.class),
                null,
                1,
                10
        );
        log.info(Instant.now().toString());
    }

    @Mapper
    public interface TruncateMapper {
        @Delete("truncate table test_client")
        void truncate();
    }
}
