package io.gardenerframework.camellia.authentication.server.test.cases;

import io.gardenerframework.camellia.authentication.server.common.api.group.AuthenticationServerAdministrationRestController;
import io.gardenerframework.camellia.authentication.server.common.configuration.AuthenticationServerPathOption;
import io.gardenerframework.camellia.authentication.server.test.AuthenticationServerEngineTestApplication;
import io.gardenerframework.camellia.authentication.server.test.utils.TokenAuthenticationClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zhanghan30
 * @date 2023/3/21 19:50
 */
@SpringBootTest(classes = AuthenticationServerEngineTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("AuthenticationServerAdministrationRestControllerGroupConfigurer测试")
@Import(AuthenticationServerAdministrationRestControllerGroupConfigurerTest
        .AuthenticationServerAdministrationRestControllerGroupConfigurerTestController.class)
public class AuthenticationServerAdministrationRestControllerGroupConfigurerTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TokenAuthenticationClient tokenAuthenticationClient;
    @Autowired
    private AuthenticationServerPathOption option;

    @AuthenticationServerAdministrationRestController
    @RequestMapping("/AuthenticationServerAdministrationRestControllerGroupConfigurerTest")
    public static class AuthenticationServerAdministrationRestControllerGroupConfigurerTestController {
        @GetMapping
        public void test() {

        }
    }

    @Test
    public void smokeTest() {
        tokenAuthenticationClient.setPort(port);
        tokenAuthenticationClient.getClientToken();
        tokenAuthenticationClient.getForObject(
                "http://localhost:{port}" + option.getAdministrationRestApiContextPath() +
                        "/AuthenticationServerAdministrationRestControllerGroupConfigurerTest",
                void.class,
                port
        );
    }
}
