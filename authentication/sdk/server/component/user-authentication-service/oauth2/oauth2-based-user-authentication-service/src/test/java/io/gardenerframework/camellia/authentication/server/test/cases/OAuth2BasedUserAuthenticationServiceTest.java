package io.gardenerframework.camellia.authentication.server.test.cases;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gardenerframework.camellia.authentication.server.main.OAuth2StateStore;
import io.gardenerframework.camellia.authentication.server.main.schema.reponse.CreateOAuth2StateResponse;
import io.gardenerframework.camellia.authentication.server.test.OAuth2BasedUserAuthenticationServiceTestApplication;
import io.gardenerframework.camellia.authentication.server.test.utils.TestOAuth2BaseUserAuthenticationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2023/3/6 18:57
 */
@SpringBootTest(classes = OAuth2BasedUserAuthenticationServiceTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OAuth2BasedUserAuthenticationServiceTest {
    @Autowired
    private OAuth2StateStore oAuth2StateStore;

    @LocalServerPort
    private int port;

    @Test
    public void smokeTest() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        CreateOAuth2StateResponse response = restTemplate.postForObject("http://localhost:{port}/authentication/state/oauth2/test", null, CreateOAuth2StateResponse.class, port);
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getState());
        Map<String, Object> map = new ObjectMapper().readValue(Base64.getDecoder().decode(response.getState()), Map.class);
        Assertions.assertTrue(oAuth2StateStore.verify(TestOAuth2BaseUserAuthenticationService.class, response.getState()));
    }
}
