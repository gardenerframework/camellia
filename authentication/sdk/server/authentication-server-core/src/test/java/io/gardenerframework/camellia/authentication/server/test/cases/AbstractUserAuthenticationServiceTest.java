package io.gardenerframework.camellia.authentication.server.test.cases;

import io.gardenerframework.camellia.authentication.server.main.exception.client.BadAuthenticationRequestParameterException;
import io.gardenerframework.camellia.authentication.server.test.AuthenticationEngineCoreTestApplication;
import io.gardenerframework.camellia.authentication.server.test.utils.TestUserAuthenticationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author zhanghan30
 * @date 2023/2/17 15:55
 */
@SpringBootTest(classes = AuthenticationEngineCoreTestApplication.class)
public class AbstractUserAuthenticationServiceTest {
    @Autowired
    private TestUserAuthenticationService authenticationService;

    @Test
    public void smokeTest() {
        this.authenticationService.setNullUsername(true);
        Assertions.assertThrowsExactly(
                BadAuthenticationRequestParameterException.class,
                () -> authenticationService.convert(null)
        );
        this.authenticationService.setNullUsername(false);
        authenticationService.convert(null);
    }
}
