package io.gardenerframework.camellia.authentication.common.client.test.cases;

import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2023/2/24 11:25
 */
@SpringBootTest
public class OAuth2RequestingClientBuilderTest {
    @Test
    public void smokeTest() {
        OAuth2RequestingClient.builder()
                .clientId(UUID.randomUUID().toString())
                .scopes(Collections.singletonList(UUID.randomUUID().toString()))
                .grantType(UUID.randomUUID().toString())
                .build();
        OAuth2RequestingClient.builder()
                .scopes(Collections.emptyList())
                .clientId(UUID.randomUUID().toString())
                .grantType(UUID.randomUUID().toString())
                .build();
    }
}
