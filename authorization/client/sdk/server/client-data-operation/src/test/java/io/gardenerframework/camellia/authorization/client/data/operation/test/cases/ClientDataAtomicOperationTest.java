package io.gardenerframework.camellia.authorization.client.data.operation.test.cases;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gardenerframework.camellia.authorization.client.data.operation.test.ClientDataAtomicOperationTestApplication;
import io.gardenerframework.camellia.authorization.client.data.operation.test.bean.TestClientDataAtomicOperation;
import io.gardenerframework.camellia.authorization.client.data.operation.test.bean.TestClientEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

/**
 * @author chris
 * @date 2023/10/24
 */
@SpringBootTest(classes = ClientDataAtomicOperationTestApplication.class)
public class ClientDataAtomicOperationTest {
    @Autowired
    private TestClientDataAtomicOperation testClientDataAtomicOperation;

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
        Assertions.assertEquals(objectMapper.writeValueAsString(recordToSave), objectMapper.writeValueAsString(saved));
    }
}
