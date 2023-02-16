package io.gardenerframework.camellia.authentication.infra.sms.client.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.gardenerframework.fragrans.api.options.schema.ApiOption;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @author zhanghan30
 * @date 2023/2/16 11:28
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiOption(readonly = false)
public class JdCloudSmsAuthenticationClientSecurityOption {
    /**
     * key id
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    private String accessKeyId;
    /**
     * key
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    private String accessKey;
}
