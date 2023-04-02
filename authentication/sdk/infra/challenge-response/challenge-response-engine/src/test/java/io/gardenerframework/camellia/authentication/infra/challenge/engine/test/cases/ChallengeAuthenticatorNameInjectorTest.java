package io.gardenerframework.camellia.authentication.infra.challenge.engine.test.cases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeAuthenticatorNameProvider;
import io.gardenerframework.camellia.authentication.infra.challenge.core.exception.ChallengeInCooldownException;
import io.gardenerframework.camellia.authentication.infra.challenge.core.exception.ChallengeResponseServiceException;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.test.ChallengeResponseEngineTestApplication;
import io.gardenerframework.camellia.authentication.infra.challenge.engine.utils.ChallengeAuthenticatorUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2023/2/28 12:31
 */
@Slf4j
@SpringBootTest(classes = ChallengeResponseEngineTestApplication.class)
public class ChallengeAuthenticatorNameInjectorTest {
    @Autowired
    private AbstractChallengeResponseServiceTest.TestChallengeResponseService challengeResponseService;

    @Test
    public void smokeTest() throws ChallengeResponseServiceException, ChallengeInCooldownException, JsonProcessingException {
        String appId = UUID.randomUUID().toString();
        RequestingClient client = OAuth2RequestingClient.builder().clientId(appId).grantType(UUID.randomUUID().toString()).scopes(Collections.EMPTY_SET).build();
        String requestId = UUID.randomUUID().toString();
        AbstractChallengeResponseServiceTest.TestChallengeRequest request = AbstractChallengeResponseServiceTest.TestChallengeRequest.builder().requestId(requestId).saveInContext(UUID.randomUUID().toString()).build();
        Challenge challenge = ChallengeAuthenticatorUtils.injectChallengeAuthenticatorName(challengeResponseService.sendChallenge(
                client,
                AbstractChallengeResponseServiceTest.AbstractChallengeResponseServiceTestScenario.class,
                request
        ), challengeResponseService.getChallengeAuthenticatorName());
        Assertions.assertInstanceOf(ChallengeAuthenticatorNameProvider.class, challenge);
        Assertions.assertNotNull(challenge.getId());
        Assertions.assertNotNull(challenge.getExpiryTime());
        String json = new ObjectMapper().writeValueAsString(challenge);
        log.info(json);
        Assertions.assertEquals(challengeResponseService.getChallengeAuthenticatorName(), ((ChallengeAuthenticatorNameProvider) challenge).getChallengeAuthenticatorName());
    }
}
