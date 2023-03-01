package com.jdcloud.gardener.camellia.authorization.test.utils;

import com.jdcloud.gardener.camellia.authorization.authentication.main.client.ClientGroupProvider;
import com.jdcloud.gardener.camellia.authorization.authentication.main.client.schema.PreservedClientGroups;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.stereotype.Component;

/**
 * @author ZhangHan
 * @date 2022/5/14 21:34
 */
@Component
public class SimpleClientGroupProvider implements ClientGroupProvider {

    @Override
    public String getClientGroup(@Nullable RegisteredClient registeredClient) {
        return registeredClient == null ? PreservedClientGroups.WEB : registeredClient.getClientId();
    }
}
