package io.gardenerframework.camellia.authentication.server.test.utils;

import lombok.Setter;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

/**
 * @author ZhangHan
 * @date 2022/5/13 13:49
 */
@Component
public class TokenAuthenticationClient {
    private final String clientId = "test";
    private final String clientSecret = "123";
    private final RestTemplate restTemplate;
    @Setter
    private int port;
    @Setter
    private String token;

    public TokenAuthenticationClient() {
        this.restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        this.restTemplate.setInterceptors(
                Collections.singletonList(
                        (request, body, execution) -> {
                            String authorization = "Authorization";
                            if (StringUtils.hasText(token)) {
                                request.getHeaders().add(authorization, "Bearer " + token);
                            } else {
                                request.getHeaders().add(authorization, "Basic " + Base64.getEncoder().encodeToString(String.format("%s:%s", clientId, clientSecret).getBytes(StandardCharsets.UTF_8)));
                            }
                            return execution.execute(request, body);
                        }
                )
        );
    }

    public ResponseEntity<String> login(String authenticationType, @Nullable Map<String, Object> parameters) {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.put("grant_type", Collections.singletonList("user_authentication"));
        params.put("authenticationType", Collections.singletonList(authenticationType));
        params.put("scope", Collections.singletonList("openid profile"));
        if (!CollectionUtils.isEmpty(parameters)) {
            parameters.forEach(
                    (k, v) -> params.put(k, Collections.singletonList(v))
            );
        }
        return restTemplate.postForEntity("http://localhost:{port}/oauth2/token", new HttpEntity<>(params), String.class, port);
    }

    public Map<?, ?> getUserInfo(String token) {
        this.setToken(token);
        Map forObject = restTemplate.getForObject("http://localhost:{port}/userinfo", Map.class, port);
        this.setToken(null);
        return forObject;
    }
}
