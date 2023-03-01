package com.jdcloud.gardener.camellia.authorization.authentication.main.client;

import com.jdcloud.gardener.camellia.authorization.authentication.main.client.schema.PreservedClientGroups;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author ZhangHan
 * @date 2022/4/26 8:05
 */
@Component
public class DefaultClientGroupProvider implements ClientGroupProvider {
    @Override
    public String getClientGroup(RegisteredClient registeredClient) {
        if (registeredClient != null) {
            Assert.isNull(registeredClient.getClientSecret(), "ClientGroupProviderProxy doest not work");
            return registeredClient.getClientId();
        }
        return PreservedClientGroups.WEB;
    }
}
