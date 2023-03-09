package io.gardenerframework.camellia.authentication.infra.challenge.engine.support;

import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import org.springframework.lang.Nullable;

public interface NullRequestingClientIdProvider {
    String CLIENT_ID = "null";

    default String getClientId(
            @Nullable RequestingClient client
    ) {
        return client == null ? CLIENT_ID : client.getClientId();
    }
}
