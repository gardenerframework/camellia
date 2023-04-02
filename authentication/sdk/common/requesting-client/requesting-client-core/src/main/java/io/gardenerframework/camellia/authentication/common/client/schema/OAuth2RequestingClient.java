package io.gardenerframework.camellia.authentication.common.client.schema;

import io.gardenerframework.camellia.authentication.common.data.serialization.SerializationVersionNumber;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashSet;

/**
 * 特指基于oauth2协议的，正在请求接口或服务或资源的客户端信息
 */
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class OAuth2RequestingClient extends RequestingClient {
    private static final long serialVersionUID = SerializationVersionNumber.version;
    /**
     * 访问的授权类型
     */
    @NotBlank
    @Builder.Default
    private String grantType = "";
    /**
     * 对用户信息的访问范围
     */
    @NotNull
    @Builder.Default
    private Collection<@Valid @NotBlank String> scopes = new HashSet<>();
}
