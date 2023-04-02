package io.gardenerframework.camellia.authentication.common.client.schema;

import io.gardenerframework.camellia.authentication.common.data.serialization.SerializationVersionNumber;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 特指基于oauth2协议的，正在请求接口或服务或资源的客户端信息
 */
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public abstract class RequestingClient implements Serializable {
    private static final long serialVersionUID = SerializationVersionNumber.version;
    /**
     * client id
     */
    @NotBlank
    @Builder.Default
    private String clientId = "";
}
