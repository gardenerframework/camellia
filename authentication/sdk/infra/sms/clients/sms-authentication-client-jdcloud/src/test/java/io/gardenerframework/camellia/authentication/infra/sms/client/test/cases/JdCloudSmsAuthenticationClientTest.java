package io.gardenerframework.camellia.authentication.infra.sms.client.test.cases;

import io.gardenerframework.camellia.authentication.infra.common.Scenario;
import io.gardenerframework.camellia.authentication.infra.sms.client.test.JdCloudSmsAuthenticationClientTestApplication;
import io.gardenerframework.camellia.authentication.infra.sms.core.SmsAuthenticationClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2023/2/16 11:45
 */
@SpringBootTest(classes = JdCloudSmsAuthenticationClientTestApplication.class)
public class JdCloudSmsAuthenticationClientTest {
    @Autowired
    private SmsAuthenticationClient smsAuthenticationClient;

    @Test
    public void smokeTest() throws Exception {
        smsAuthenticationClient.sendCode(
                UUID.randomUUID().toString(),
                "13581837282",
                TestScenario.class,
                "123456"
        );
    }

    public static class TestScenario implements Scenario {

    }
}
