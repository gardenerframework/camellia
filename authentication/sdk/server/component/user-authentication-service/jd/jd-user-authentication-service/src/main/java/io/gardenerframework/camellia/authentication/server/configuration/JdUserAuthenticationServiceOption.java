package io.gardenerframework.camellia.authentication.server.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.gardenerframework.fragrans.api.options.schema.ApiOption;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @author zhanghan30
 * @date 2023/3/9 07:47
 */
@ApiOption(readonly = false)
@Getter
@Setter
@JdUserAuthenticationServiceComponent
public class JdUserAuthenticationServiceOption {
    /**
     * app id
     */
    @NotBlank
    private String appId;
    /**
     * app密码
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    private String appSecret;
}
