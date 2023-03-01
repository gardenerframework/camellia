package io.gardenerframework.camellia.authentication.server.test.cases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gardenerframework.camellia.authentication.server.main.annotation.AuthenticationType;
import io.gardenerframework.camellia.authentication.server.test.AuthenticationServerEngineTestApplication;
import io.gardenerframework.camellia.authentication.server.test.authentication.main.MfaTriggerRequest;
import io.gardenerframework.camellia.authentication.server.test.utils.TokenAuthenticationClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

/**
 * @author ZhangHan
 * @date 2022/5/17 19:44
 */
@SpringBootTest(classes = AuthenticationServerEngineTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("refresh token测试")
public class RefreshTokenTest {
    private final RestTemplate restTemplate;
    @LocalServerPort
    private int port;
    @Autowired
    private TokenAuthenticationClient authenticationClient;

    public RefreshTokenTest() {
        this.restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        this.restTemplate.setInterceptors(
                Collections.singletonList(
                        (request, body, execution) -> {
                            String authorization = "Authorization";
                            request.getHeaders().add(authorization, "Basic " + Base64.getEncoder().encodeToString(String.format("%s:%s", "test", "123").getBytes(StandardCharsets.UTF_8)));
                            return execution.execute(request, body);
                        }
                )
        );
    }

    @Test
    @DisplayName("冒烟测试")
    public void smokeTest() throws JsonProcessingException {
        authenticationClient.setPort(port);
        Map<String, Object> request = new HashMap<>();
        String username = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        request.put("username", username);
        request.put("password", password);
        ResponseEntity<String> login = authenticationClient.login(AnnotationUtils.findAnnotation(MfaTriggerRequest.class, AuthenticationType.class).value(), request);
        Map<String, Object> token = new ObjectMapper().readValue(login.getBody(), new TypeReference<Map<String, Object>>() {
        });
        Assertions.assertNotNull(token.get("refresh_token"));
        String refreshToken = (String) token.get("refresh_token");
        MultiValueMap<String, String> refreshRequest = new LinkedMultiValueMap<>();
        refreshRequest.put("refresh_token", Collections.singletonList(refreshToken));
        refreshRequest.put("grant_type", Collections.singletonList("refresh_token"));
        refreshRequest.put("scope", Collections.singletonList("openid profile"));
        Map map = restTemplate.postForObject("http://localhost:{port}/oauth2/token", refreshRequest, Map.class, port);
        Assertions.assertNotNull(map.get("access_token"));
        Map<?, ?> userInfo = authenticationClient.getUserInfo((String) map.get("access_token"));
        Assertions.assertEquals(username, userInfo.get("sub"));
        authenticationClient.setToken(null);
    }

    @Test
    @DisplayName("refresh token ttl测试")
    public void refreshTokenTtl() throws JsonProcessingException {
        authenticationClient.setPort(port);
        Map<String, Object> request = new HashMap<>();
        String username = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        request.put("username", username);
        request.put("password", password);
        ResponseEntity<String> login = authenticationClient.login(AnnotationUtils.findAnnotation(MfaTriggerRequest.class, AuthenticationType.class).value(), request);
        Map<String, Object> token = new ObjectMapper().readValue(login.getBody(), new TypeReference<Map<String, Object>>() {
        });
        Assertions.assertNotNull(token.get("refresh_token"));
        String refreshToken = (String) token.get("refresh_token");
        MultiValueMap<String, Object> refreshRequest = new LinkedMultiValueMap<>();
        refreshRequest.put("refresh_token", Collections.singletonList(refreshToken));
        refreshRequest.put("grant_type", Collections.singletonList("refresh_token"));
        refreshRequest.put("scope", Collections.singletonList("openid profile"));
        refreshRequest.put("token_ttl", Collections.singletonList(Duration.ofMinutes(1).getSeconds()));
        Map map = restTemplate.postForObject("http://localhost:{port}/oauth2/token", refreshRequest, Map.class, port);
        Assertions.assertNotNull(map.get("access_token"));
        Assertions.assertTrue((int) map.get("expires_in") <= Duration.ofMinutes(1).getSeconds());
        Map<?, ?> userInfo = authenticationClient.getUserInfo((String) map.get("access_token"));
        Assertions.assertEquals(username, userInfo.get("sub"));
        authenticationClient.setToken(null);
    }
}
