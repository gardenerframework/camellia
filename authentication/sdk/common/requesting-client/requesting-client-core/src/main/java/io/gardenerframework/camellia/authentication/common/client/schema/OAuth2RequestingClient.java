package io.gardenerframework.camellia.authentication.common.client.schema;

import io.gardenerframework.camellia.authentication.common.data.serialization.SerializationVersionNumber;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;

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
    private String grantType;
    /**
     * 对用户信息的访问范围
     */
    @NotNull
    @Singular
    private Collection<@Valid @NotBlank String> scopes;
}
