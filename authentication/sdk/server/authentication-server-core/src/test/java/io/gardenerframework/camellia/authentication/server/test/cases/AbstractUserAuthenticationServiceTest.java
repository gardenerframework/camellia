package io.gardenerframework.camellia.authentication.server.test.cases;

import io.gardenerframework.camellia.authentication.server.main.exception.client.BadAuthenticationRequestParameterException;
import io.gardenerframework.camellia.authentication.server.test.AuthenticationEngineCoreTestApplication;
import io.gardenerframework.camellia.authentication.server.test.utils.TestUserAuthenticationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * @author zhanghan30
 * @date 2023/2/17 15:55
 */
@SpringBootTest(classes = AuthenticationEngineCoreTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(AbstractUserAuthenticationServiceTest.AbstractUserAuthenticationServiceTestController.class)
public class AbstractUserAuthenticationServiceTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestUserAuthenticationService authenticationService;

    @Test
    public void smokeTest() {
        new RestTemplate().getForObject(
                "http://localhost:{port}/AbstractUserAuthenticationServiceTest",
                void.class,
                port
        );
    }

    @RestController
    @RequestMapping("/AbstractUserAuthenticationServiceTest")
    public static class AbstractUserAuthenticationServiceTestController {
        @Autowired
        private TestUserAuthenticationService authenticationService;

        @GetMapping
        public void smokeTest(HttpServletRequest request) {
            this.authenticationService.setNullUsername(true);
            Assertions.assertThrowsExactly(
                    BadAuthenticationRequestParameterException.class,
                    () -> authenticationService.convert(request, null, new HashMap<>())
            );
            this.authenticationService.setNullUsername(false);
            authenticationService.convert(request, null, new HashMap<>());
        }
    }
}
