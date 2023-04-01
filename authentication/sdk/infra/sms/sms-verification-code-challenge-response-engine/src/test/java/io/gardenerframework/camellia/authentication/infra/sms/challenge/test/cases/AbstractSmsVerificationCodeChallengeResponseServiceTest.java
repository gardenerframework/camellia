package io.gardenerframework.camellia.authentication.infra.sms.challenge.test.cases;

import io.gardenerframework.camellia.authentication.infra.sms.challenge.test.SmsVerificationCodeChallengeResponseServiceTestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = SmsVerificationCodeChallengeResponseServiceTestApplication.class)
public class AbstractSmsVerificationCodeChallengeResponseServiceTest {

    @Test
    public void smokeTest() {
    }
}
