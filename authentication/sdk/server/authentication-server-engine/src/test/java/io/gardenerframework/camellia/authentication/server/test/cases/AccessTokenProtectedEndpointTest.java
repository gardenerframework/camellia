//package com.jdcloud.gardener.camellia.authorization.test.cases;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.jdcloud.gardener.camellia.authorization.authentication.main.annotation.AuthenticationType;
//import com.jdcloud.gardener.camellia.authorization.test.AuthorizationServerEngineTestApplication;
//import com.jdcloud.gardener.camellia.authorization.test.authentication.main.MfaTriggerRequest;
//import com.jdcloud.gardener.camellia.authorization.test.utils.TokenAuthenticationClient;
//import lombok.Setter;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.web.server.LocalServerPort;
//import org.springframework.core.annotation.AnnotationUtils;
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
//import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeAuthenticationProvider;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//
//import java.nio.charset.StandardCharsets;
//import java.util.*;
//
///**
// * @author ZhangHan
// * @date 2022/5/17 23:18
// */
//@SpringBootTest(classes = AuthorizationServerEngineTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@DisplayName("AccessTokenProtectedEndpoint接口测试")
//public class AccessTokenProtectedEndpointTest {
//    private final RestTemplate restTemplate;
//    @LocalServerPort
//    private int port;
//    @Autowired
//    private TokenAuthenticationClient authenticationClient;
//    @Setter
//    private String token;
//
//    public AccessTokenProtectedEndpointTest() {
//        this.restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
//        this.restTemplate.setInterceptors(
//                Collections.singletonList(
//                        (request, body, execution) -> {
//                            String authorization = "Authorization";
//                            if (this.token == null) {
//                                request.getHeaders().add(authorization, "Basic " + Base64.getEncoder().encodeToString(String.format("%s:%s", "test", "123").getBytes(StandardCharsets.UTF_8)));
//                            } else {
//                                request.getHeaders().add(authorization, "Bearer " + token);
//                            }
//                            return execution.execute(request, body);
//                        }
//                )
//        );
//    }
//
//    @Test
//    @DisplayName("测试客户端凭据")
//    public void testClientCredentials() {
//        MultiValueMap<String, String> clientCredentials = new LinkedMultiValueMap<>();
//        clientCredentials.put("grant_type", Collections.singletonList("client_credentials"));
//        clientCredentials.put("scope", Collections.singletonList("openid profile"));
//        Map map = restTemplate.postForObject("http://localhost:{port}/oauth2/token", clientCredentials, HashMap.class, port);
//        String accessToken = (String) map.get("access_token");
//        this.setToken(accessToken);
//        Map accessTokenDetails = restTemplate.getForObject("http://localhost:{port}/api/TestAccessTokenProtectedEndpoint", HashMap.class, port);
//        Assertions.assertNotNull(accessTokenDetails.get("client"));
//        Assertions.assertEquals("test", ((Map) accessTokenDetails.get("client")).get("clientId"));
//        this.setToken(null);
//    }
//
//    /**
//     * {@link OAuth2AuthorizationCodeAuthenticationProvider} 没测，因为太麻烦，但逻辑上不会有问题
//     *
//     * @throws JsonProcessingException
//     */
//    @Test
//    @DisplayName("测试使用引擎实现的token granter")
//    public void testTokenAuthentication() throws JsonProcessingException {
//        authenticationClient.setPort(port);
//        Map<String, Object> request = new HashMap<>();
//        String username = UUID.randomUUID().toString();
//        String password = UUID.randomUUID().toString();
//        request.put("username", username);
//        request.put("password", password);
//        //获取token
//        ResponseEntity<String> login = authenticationClient.login(Objects.requireNonNull(AnnotationUtils.findAnnotation(MfaTriggerRequest.class, AuthenticationType.class)).value(), request);
//        Map<String, Object> token = new ObjectMapper().readValue(login.getBody(), new TypeReference<Map<String, Object>>() {
//        });
//        Assertions.assertNotNull(token.get("access_token"));
//        this.setToken((String) token.get("access_token"));
//        Map accessTokenDetails = restTemplate.getForObject("http://localhost:{port}/api/TestAccessTokenProtectedEndpoint", HashMap.class, port);
//        Assertions.assertNotNull(accessTokenDetails.get("client"));
//        Assertions.assertEquals("test", ((Map) accessTokenDetails.get("client")).get("clientId"));
//        this.setToken(null);
//    }
//}
