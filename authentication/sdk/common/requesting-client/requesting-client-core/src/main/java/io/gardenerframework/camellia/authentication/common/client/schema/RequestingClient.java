package io.gardenerframework.camellia.authentication.common.client.schema;

import io.gardenerframework.camellia.authentication.common.data.serialization.SerializationVersionNumber;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 特指基于oauth2协议的，正在请求接口或服务或资源的客户端信息
 */
@SuperBuilder
@Getter
@NoArgsConstructor
public abstract class RequestingClient implements Serializable {
    private static final long serialVersionUID = SerializationVersionNumber.version;
    /**
     * client id
     */
    @NonNull
    private String clientId;
}
