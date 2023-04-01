package io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.test.cases;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.client.MfaAuthenticationClientPrototype;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.request.CloseChallengeRequest;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.request.SendChallengeRequest;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.response.ListAuthenticatorsResponse;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.test.MfaAuthenticationClientTestApplication;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.test.bean.TestChallenge;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.test.bean.TestScenario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2023/3/30 14:15
 */
@SpringBootTest(classes = MfaAuthenticationClientTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EnableFeignClients(clients = MfaAuthenticationClientTest.TestChallengeClient.class)
public class MfaAuthenticationClientTest {
    @Autowired
    private TestChallengeClient testChallengeClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void smokeTest() throws Exception {
        ListAuthenticatorsResponse listAuthenticatorsResponse = testChallengeClient.listAuthenticators();
        Assertions.assertTrue(listAuthenticatorsResponse.getAuthenticators().contains("test"));
        OAuth2RequestingClient client = new OAuth2RequestingClient();
        client.setClientId(UUID.randomUUID().toString());
        client.setGrantType(UUID.randomUUID().toString());
        client.setScopes(Collections.emptySet());
        SendChallengeRequest request = new SendChallengeRequest(
                new HashMap<>(),
                objectMapper.convertValue(client, Map.class),
                TestScenario.class.getName(),
                null
        );
        TestChallenge challenge = testChallengeClient.sendChallenge(
                "test",
                request
        );
        Assertions.assertNotNull(challenge.getId());
        Assertions.assertNotNull(challenge.getField());
        Assertions.assertEquals(TestScenario.class.getName(), challenge.getField());
        testChallengeClient.closeChallenge("test", new CloseChallengeRequest(
                objectMapper.convertValue(client, Map.class),
                TestScenario.class.getName(),
                challenge.getId()
        ));
    }

    @FeignClient(name = "mfa-authentication", decode404 = true)
    public interface TestChallengeClient extends MfaAuthenticationClientPrototype<TestChallenge> {

    }
}
