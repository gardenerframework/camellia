package io.gardenerframework.camellia.authentication.infra.sms.challenge.test.cases;

import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.challenge.core.exception.ChallengeInCooldownException;
import io.gardenerframework.camellia.authentication.infra.challenge.core.exception.ChallengeResponseServiceException;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.schema.SmsVerificationCodeChallengeRequest;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.test.SmsVerificationCodeChallengeResponseServiceTestApplication;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.test.beans.TestSmsVerificationCodeChallengeResponseService;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.test.beans.TestSmsVerificationCodeClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest(classes = SmsVerificationCodeChallengeResponseServiceTestApplication.class)
public class AbstractSmsVerificationCodeChallengeResponseServiceTest implements Scenario {
    @Autowired
    private TestSmsVerificationCodeChallengeResponseService testSmsVerificationCodeChallengeResponseService;

    @Autowired
    private TestSmsVerificationCodeClient testSmsVerificationCodeClient;

    @Test
    public void smokeTest() throws ChallengeResponseServiceException, ChallengeInCooldownException {
        Challenge challenge = testSmsVerificationCodeChallengeResponseService.sendChallenge(
                null,
                AbstractSmsVerificationCodeChallengeResponseServiceTest.class,
                SmsVerificationCodeChallengeRequest.builder()
                        .mobilePhoneNumber(UUID.randomUUID().toString())
                        .build()
        );
        testSmsVerificationCodeChallengeResponseService.verifyResponse(
                null,
                AbstractSmsVerificationCodeChallengeResponseServiceTest.class,
                challenge.getId(),
                testSmsVerificationCodeClient.getCode()
        );
    }
}
