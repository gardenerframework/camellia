package io.gardenerframework.camellia.authentication.server.test.cases;

import io.gardenerframework.camellia.authentication.server.test.AdministrationServerTestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

/**
 * @author zhanghan30
 * @date 2023/3/22 18:10
 */
@SpringBootTest(classes = AdministrationServerTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdministrationServerRestControllerTest {
    @LocalServerPort
    private int port;

    @Test
    public void smokeTest() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForObject(
                "http://localhost:{port}/api/TestAdministrationServerRestController",
                void.class,
                port
        );
    }
}
