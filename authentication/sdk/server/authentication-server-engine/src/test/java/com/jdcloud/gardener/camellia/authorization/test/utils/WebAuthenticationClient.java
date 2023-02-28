package com.jdcloud.gardener.camellia.authorization.test.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

/**
 * @author ZhangHan
 * @date 2022/5/13 11:17
 */
@AuthenticationServerEngineComponent
public class WebAuthenticationClient {
    private final RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<Void> login(String authenticationType, @Nullable Map<String, Object> parameters) {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.put("authenticationType", Collections.singletonList(authenticationType));
        if (!CollectionUtils.isEmpty(parameters)) {
            parameters.forEach(
                    (k, v) -> params.put(k, Collections.singletonList(v))
            );
        }
        return restTemplate.postForEntity("http://localhost:19090/login", new HttpEntity<>(params), void.class);
    }
}
