package com.jdcloud.gardener.camellia.authorization.test.cases;

import com.jayway.jsonpath.JsonPath;
import com.jdcloud.gardener.camellia.authorization.authentication.main.UserAuthenticationService;
import com.jdcloud.gardener.camellia.authorization.authentication.main.annotation.AuthenticationType;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.UserAuthenticationRequestToken;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.UsernamePrincipal;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
import com.jdcloud.gardener.camellia.authorization.test.AuthorizationServerEngineTestApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2022/4/25 1:37 下午
 */
@SpringBootTest(classes = AuthorizationServerEngineTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("认证转换器接口测试")
@Import(value = {UserAuthenticationServiceManagementEndpointTest.AuthenticationRequestConverterEndpointTestConverter.class})
public class UserAuthenticationServiceManagementEndpointTest {
    private final RestTemplate restTemplate;
    @LocalServerPort
    private int randomServerPort;
    private String token;

    public UserAuthenticationServiceManagementEndpointTest() {
        this.restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        this.restTemplate.setInterceptors(
                Collections.singletonList(
                        (request, body, execution) -> {
                            String authorization = "Authorization";
                            if (StringUtils.hasText(token)) {
                                request.getHeaders().add(authorization, "Bearer " + token);
                            } else {
                                request.getHeaders().add(authorization, "Basic " + Base64.getEncoder().encodeToString(String.format("%s:%s", "test", "123").getBytes(StandardCharsets.UTF_8)));
                            }
                            return execution.execute(request, body);
                        }
                )
        );
    }

    @Test
    @DisplayName("冒烟测试")
    public void simpleSmokeTest() {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.put("grant_type", Collections.singletonList("client_credentials"));
        Map accessTokenResponse = this.restTemplate.postForObject("http://localhost:{port}/oauth2/token", new HttpEntity<>(params), Map.class, randomServerPort);
        this.token = (String) accessTokenResponse.get("access_token");
        //正常读取
        String options = restTemplate.getForObject("http://localhost:" + randomServerPort + "/api/options", String.class);
        List<String> types = JsonPath.read(options, "$.options.authenticationTypeRegistry.option.types");
        Assertions.assertNotNull(types);
        Assertions.assertTrue(types.contains("AuthenticationRequestConverterEndpointTestConverter"));
        //禁用
        restTemplate.postForObject("http://localhost:" + randomServerPort + "/api/authentication/service/AuthenticationRequestConverterEndpointTestConverter:disable", null, void.class);
        options = restTemplate.getForObject("http://localhost:" + randomServerPort + "/api/options", String.class);
        types = JsonPath.read(options, "$.options.authenticationTypeRegistry.option.types");
        Assertions.assertNotNull(types);
        Assertions.assertFalse(types.contains("AuthenticationRequestConverterEndpointTestConverter"));
        //启用
        restTemplate.postForObject("http://localhost:" + randomServerPort + "/api/authentication/service/AuthenticationRequestConverterEndpointTestConverter:enable", null, void.class);
        options = restTemplate.getForObject("http://localhost:" + randomServerPort + "/api/options", String.class);
        types = JsonPath.read(options, "$.options.authenticationTypeRegistry.option.types");
        Assertions.assertNotNull(types);
        Assertions.assertTrue(types.contains("AuthenticationRequestConverterEndpointTestConverter"));
        this.token = null;
    }

    @Component
    @AuthenticationType(value = "AuthenticationRequestConverterEndpointTestConverter")
    public static class AuthenticationRequestConverterEndpointTestConverter implements UserAuthenticationService {

        @Override
        public UserAuthenticationRequestToken convert(HttpServletRequest request) {
            return new UserAuthenticationRequestToken(
                    new UsernamePrincipal(request.getParameter("username")),
                    null
            );
        }

        @Override
        public void authenticate(UserAuthenticationRequestToken authenticationRequest, User user) throws AuthenticationException {

        }
    }
}
