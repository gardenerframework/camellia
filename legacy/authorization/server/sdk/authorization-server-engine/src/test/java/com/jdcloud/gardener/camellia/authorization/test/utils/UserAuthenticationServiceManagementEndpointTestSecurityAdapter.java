package com.jdcloud.gardener.camellia.authorization.test.utils;

import com.jdcloud.gardener.camellia.authorization.authentication.management.endpoint.UserAuthenticationServiceManagementEndpointSkeleton;
import com.jdcloud.gardener.camellia.authorization.common.api.security.schema.AccessTokenDetails;
import com.jdcloud.gardener.fragrans.api.advice.engine.EndpointHandlerMethodBeforeAdviceAdapter;
import com.jdcloud.gardener.fragrans.api.standard.error.exception.client.ForbiddenException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author ZhangHan
 * @date 2022/5/15 0:00
 */
@Component
@AllArgsConstructor
public class UserAuthenticationServiceManagementEndpointTestSecurityAdapter extends EndpointHandlerMethodBeforeAdviceAdapter implements UserAuthenticationServiceManagementEndpointSkeleton {
    private final AccessTokenDetails accessTokenDetails;

    @Override
    public void enableService(String type) {
        if (accessTokenDetails.getClient() == null || !Objects.equals(accessTokenDetails.getClient().getClientId(), "test")) {
            throw new ForbiddenException();
        }
    }

    @Override
    public void disableService(String type) {
        if (accessTokenDetails.getClient() == null || !Objects.equals(accessTokenDetails.getClient().getClientId(), "test")) {
            throw new ForbiddenException();
        }
    }
}
