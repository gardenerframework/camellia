package io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.test.cases;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.request.SendChallengeRequest;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.request.VerifyResponseRequest;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.response.ListAuthenticatorsResponse;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.test.MfaAuthenticationServerEngineTestApplication;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.test.bean.TestChallenge;
import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.test.bean.TestScenario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2023/3/29 17:48
 */
@SpringBootTest(classes = MfaAuthenticationServerEngineTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MfaAuthenticationServerEngineTest {
    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void smokeTest() {
        RestTemplate restTemplate = new RestTemplate();
        ListAuthenticatorsResponse listAuthenticatorsResponse = restTemplate.getForObject(
                "http://localhost:{port}/mfa",
                ListAuthenticatorsResponse.class,
                port
        );
        Assertions.assertTrue(listAuthenticatorsResponse.getAuthenticators().contains("test"));
        OAuth2RequestingClient client = new OAuth2RequestingClient();
        client.setClientId(UUID.randomUUID().toString());
        client.setGrantType(UUID.randomUUID().toString());
        client.setScopes(Collections.emptySet());
        TestChallenge challenge = restTemplate.postForObject(
                "http://localhost:{port}/mfa/test:send",
                new SendChallengeRequest(
                        new HashMap<>(),
                        objectMapper.convertValue(client, Map.class),
                        TestScenario.class.getName(),
                        null),
                TestChallenge.class,
                port
        );
        Assertions.assertNotNull(challenge.getId());
        Assertions.assertNotNull(challenge.getField());
        Assertions.assertEquals(TestScenario.class.getName(), challenge.getField());
        restTemplate.postForObject(
                "http://localhost:{port}/mfa/test:verify",
                new VerifyResponseRequest(
                        objectMapper.convertValue(client, Map.class),
                        TestScenario.class.getName(),
                        challenge.getId(),
                        UUID.randomUUID().toString()),
                void.class,
                port
        );
    }
}
