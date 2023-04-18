package io.gardenerframework.camellia.authentication.common.client.test.cases;

import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.Validator;
import java.util.Collections;
import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2023/2/24 11:25
 */
@SpringBootTest
public class OAuth2RequestingClientBuilderTest {
    @Autowired
    public Validator validator;

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
        Assertions.assertFalse(validator.validate(OAuth2RequestingClient.builder()
                .scopes(Collections.singletonList(""))
                .clientId(UUID.randomUUID().toString())
                .grantType(UUID.randomUUID().toString())
                .build()).isEmpty());

    }
}
