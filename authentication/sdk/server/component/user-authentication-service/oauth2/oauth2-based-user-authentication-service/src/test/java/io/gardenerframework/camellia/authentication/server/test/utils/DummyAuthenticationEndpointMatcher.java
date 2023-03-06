package io.gardenerframework.camellia.authentication.server.test.utils;

import io.gardenerframework.camellia.authentication.server.main.utils.AuthenticationEndpointMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhanghan30
 * @date 2023/3/6 19:16
 */
@Component
public class DummyAuthenticationEndpointMatcher implements AuthenticationEndpointMatcher {
    @Override
    public boolean isTokenEndpoint(HttpServletRequest request) {
        return true;
    }

    @Override
    public boolean isWebAuthenticationEndpoint(HttpServletRequest request) {
        return true;
    }
}
