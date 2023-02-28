package com.jdcloud.gardener.camellia.authorization.test.authentication.endpoint;

import com.jdcloud.gardener.camellia.authorization.common.api.group.AuthorizationServerRestController;
import com.jdcloud.gardener.camellia.authorization.common.api.security.AccessTokenProtectedEndpoint;
import com.jdcloud.gardener.camellia.authorization.common.api.security.schema.AccessTokenDetails;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZhangHan
 * @date 2022/5/17 23:16
 */
@AuthorizationServerRestController
@AuthenticationServerEngineComponent
@RequestMapping("/TestAccessTokenProtectedEndpoint")
@AllArgsConstructor
public class TestAccessTokenProtectedEndpoint {
    private final AccessTokenDetails accessTokenDetails;

    @AccessTokenProtectedEndpoint()
    @GetMapping
    public Map<String, Object> accessTokenDetails() {
        Map<String, Object> details = new HashMap<>();
        details.put("client", accessTokenDetails.getClient());
        details.put("user", accessTokenDetails.getUser());
        return details;
    }
}
