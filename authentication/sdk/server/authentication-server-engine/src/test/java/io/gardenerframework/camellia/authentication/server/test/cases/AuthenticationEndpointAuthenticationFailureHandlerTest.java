package io.gardenerframework.camellia.authentication.server.test.cases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gardenerframework.camellia.authentication.server.main.exception.OAuth2ErrorCodes;
import io.gardenerframework.camellia.authentication.server.main.exception.SpringHardCodedErrors;
import io.gardenerframework.camellia.authentication.server.test.AuthenticationServerEngineTestApplication;
import io.gardenerframework.fragrans.messages.EnhancedMessageSource;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author ZhangHan
 * @date 2022/5/20 0:42
 */
@SpringBootTest(classes = AuthenticationServerEngineTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("AuthenticationEndpointAuthenticationFailureHandler测试")
public class AuthenticationEndpointAuthenticationFailureHandlerTest {
    @LocalServerPort
    private int port;
    @Autowired
    private EnhancedMessageSource messageSource;

    @Test
    public void invalidClientTest() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        restTemplate.setInterceptors(
                Collections.singletonList(
                        (request, body, execution) -> {
                            String authorization = "Authorization";
                            request.getHeaders().add(authorization, "Basic " + Base64.getEncoder().encodeToString(String.format("%s:%s", "test", "567").getBytes(StandardCharsets.UTF_8)));
                            return execution.execute(request, body);
                        }
                )
        );
        try {
            restTemplate.postForObject("http://localhost:{port}/oauth2/token", null, String.class, port);
        } catch (HttpClientErrorException e) {
            Assertions.assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
            OAuth2Error oAuth2Error = new ObjectMapper().readValue(e.getResponseBodyAsString(), OAuth2Error.class);
            Assertions.assertEquals(OAuth2ErrorCodes.INVALID_CLIENT, oAuth2Error.getError());
            Assertions.assertNotNull(oAuth2Error.getError_description());
            Assertions.assertNotNull(oAuth2Error.getError_code());
            return;
        }
        Assertions.fail();
    }

    @Test
    public void invalidGrant() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        restTemplate.setInterceptors(
                Collections.singletonList(
                        (request, body, execution) -> {
                            String authorization = "Authorization";
                            request.getHeaders().add(authorization, "Basic " + Base64.getEncoder().encodeToString(String.format("%s:%s", "test", "123").getBytes(StandardCharsets.UTF_8)));
                            return execution.execute(request, body);
                        }
                )
        );
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        try {
            restTemplate.postForObject("http://localhost:{port}/oauth2/token", param, String.class, port);
        } catch (HttpClientErrorException e) {
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            OAuth2Error oAuth2Error = new ObjectMapper().readValue(e.getResponseBodyAsString(), OAuth2Error.class);
            Assertions.assertEquals(OAuth2ErrorCodes.INVALID_REQUEST, oAuth2Error.getError());
            Assertions.assertTrue(oAuth2Error.getError_description().startsWith(messageSource.getMessage(new SpringHardCodedErrors.OAuth2ParameterError(""), Locale.getDefault())));
            return;
        }
        Assertions.fail();
    }

    @Test
    public void invalidParameter() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        restTemplate.setInterceptors(
                Collections.singletonList(
                        (request, body, execution) -> {
                            String authorization = "Authorization";
                            request.getHeaders().add(authorization, "Basic " + Base64.getEncoder().encodeToString(String.format("%s:%s", "test", "123").getBytes(StandardCharsets.UTF_8)));
                            return execution.execute(request, body);
                        }
                )
        );
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.put("grant_type", Collections.singletonList("authorization_code"));
        try {
            restTemplate.postForObject("http://localhost:{port}/oauth2/token", param, String.class, port);
        } catch (HttpClientErrorException e) {
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            OAuth2Error oAuth2Error = new ObjectMapper().readValue(e.getResponseBodyAsString(), OAuth2Error.class);
            Assertions.assertEquals(OAuth2ErrorCodes.INVALID_REQUEST, oAuth2Error.getError());
            Assertions.assertTrue(oAuth2Error.getError_description().startsWith(messageSource.getMessage(new SpringHardCodedErrors.OAuth2ParameterError(""), Locale.getDefault())));
            return;
        }
        Assertions.fail();
    }

    @Test
    public void requestNonAuthorizedGrantType() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        restTemplate.setInterceptors(
                Collections.singletonList(
                        (request, body, execution) -> {
                            String authorization = "Authorization";
                            request.getHeaders().add(authorization, "Basic " + Base64.getEncoder().encodeToString(String.format("%s:%s", "no-client", "1234").getBytes(StandardCharsets.UTF_8)));
                            return execution.execute(request, body);
                        }
                )
        );
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.put("grant_type", Collections.singletonList("client_credentials"));
        try {
            restTemplate.postForObject("http://localhost:{port}/oauth2/token", param, String.class, port);
        } catch (HttpClientErrorException e) {
            Assertions.assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
            OAuth2Error oAuth2Error = new ObjectMapper().readValue(e.getResponseBodyAsString(), OAuth2Error.class);
            Assertions.assertEquals(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT, oAuth2Error.getError());
            Assertions.assertNull(oAuth2Error.getError_description());
            return;
        }
        Assertions.fail();
    }

    @Test
    public void requestNonSupportedGrantType() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        restTemplate.setInterceptors(
                Collections.singletonList(
                        (request, body, execution) -> {
                            String authorization = "Authorization";
                            request.getHeaders().add(authorization, "Basic " + Base64.getEncoder().encodeToString(String.format("%s:%s", "no-client", "1234").getBytes(StandardCharsets.UTF_8)));
                            return execution.execute(request, body);
                        }
                )
        );
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.put("grant_type", Collections.singletonList(UUID.randomUUID().toString()));
        try {
            restTemplate.postForObject("http://localhost:{port}/oauth2/token", param, String.class, port);
        } catch (HttpClientErrorException e) {
            OAuth2Error oAuth2Error = new ObjectMapper().readValue(e.getResponseBodyAsString(), OAuth2Error.class);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            Assertions.assertEquals(OAuth2ErrorCodes.UNSUPPORTED_GRANT_TYPE, oAuth2Error.getError());
            return;
        }
        Assertions.fail();
    }


    @Test
    public void requestNonAuthorizedScope() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        restTemplate.setInterceptors(
                Collections.singletonList(
                        (request, body, execution) -> {
                            String authorization = "Authorization";
                            request.getHeaders().add(authorization, "Basic " + Base64.getEncoder().encodeToString(String.format("%s:%s", "test", "123").getBytes(StandardCharsets.UTF_8)));
                            return execution.execute(request, body);
                        }
                )
        );
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.put("grant_type", Collections.singletonList("client_credentials"));
        param.put("scope", Collections.singletonList(UUID.randomUUID().toString()));
        try {
            restTemplate.postForObject("http://localhost:{port}/oauth2/token", param, String.class, port);
        } catch (HttpClientErrorException e) {
            OAuth2Error oAuth2Error = new ObjectMapper().readValue(e.getResponseBodyAsString(), OAuth2Error.class);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            Assertions.assertEquals(OAuth2ErrorCodes.INVALID_SCOPE, oAuth2Error.getError());
            Assertions.assertNull(oAuth2Error.getError_description());
            return;
        }
        Assertions.fail();
    }

    @Data
    @NoArgsConstructor
    public static class OAuth2Error {
        private String error;
        private String error_description;
        private String error_code;
        private Map<String, Object> details;
    }
}
