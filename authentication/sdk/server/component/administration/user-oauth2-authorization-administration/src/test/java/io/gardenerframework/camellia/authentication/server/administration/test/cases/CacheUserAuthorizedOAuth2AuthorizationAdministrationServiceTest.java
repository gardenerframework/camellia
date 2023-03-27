package io.gardenerframework.camellia.authentication.server.administration.test.cases;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gardenerframework.camellia.authentication.server.administration.authorization.schema.request.RemoveUserAuthorizedAuthorizationRequest;
import io.gardenerframework.camellia.authentication.server.administration.authorization.service.UserAuthorizedOAuth2AuthorizationAdministrationService;
import io.gardenerframework.camellia.authentication.server.administration.test.UserAuthorizedOAuth2AuthorizationAdministrationServiceTestApplication;
import io.gardenerframework.camellia.authentication.server.administration.test.beans.TokenAuthenticationClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2023/3/27 12:38
 */
@SpringBootTest(classes = UserAuthorizedOAuth2AuthorizationAdministrationServiceTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CacheUserAuthorizedOAuth2AuthorizationAdministrationServiceTest {
    @LocalServerPort
    private int port;
    @Autowired
    private UserAuthorizedOAuth2AuthorizationAdministrationService authorizationAdministrationService;

    @Autowired
    private TokenAuthenticationClient authenticationClient;

    @Test
    public void smokeTest() throws Exception {
        authenticationClient.setPort(port);
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("username", UUID.randomUUID().toString());
        String token = authenticationClient.login("test", parameter);
        Map<?, ?> userInfo = authenticationClient.getUserInfo(token);
        String id = (String) userInfo.get("sub");
        authorizationAdministrationService.removeOAuth2Authorization(
                RemoveUserAuthorizedAuthorizationRequest.builder().userId(id).build()
        );
        //这里会无法读取
        HttpClientErrorException exception = null;
        try {
            authenticationClient.getUserInfo(token);
        } catch (HttpClientErrorException e) {
            exception = e;
        }
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        Assertions.assertEquals("invalid_token",
                new ObjectMapper().readValue(exception.getResponseBodyAsString(), Map.class)
                        .get("error")
        );
    }
}
