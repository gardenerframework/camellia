package com.jdcloud.gardener.camellia.authorization.test.cases;

import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.response.CreateDynamicEncryptionKeyResponse;
import com.jdcloud.gardener.camellia.authorization.test.UsernamePasswordPluginTestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

/**
 * @author zhanghan30
 * @date 2022/12/26 20:24
 */
@SpringBootTest(classes = UsernamePasswordPluginTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecurityEngineTest {
    @LocalServerPort
    private int port;

    @Test
    public void smokeTest() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForObject("http://localhost:{port}/authentication/username/key", null, CreateDynamicEncryptionKeyResponse.class, port);
    }
}
