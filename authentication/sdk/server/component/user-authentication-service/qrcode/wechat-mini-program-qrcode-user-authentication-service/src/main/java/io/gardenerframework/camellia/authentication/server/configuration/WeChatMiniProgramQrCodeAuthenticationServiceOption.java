package io.gardenerframework.camellia.authentication.server.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.gardenerframework.fragrans.api.options.schema.ApiOption;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

/**
 * @author zhanghan30
 * @date 2023/3/16 18:23
 */
@ApiOption(readonly = false)
@Getter
@Setter
@NoArgsConstructor
public abstract class WeChatMiniProgramQrCodeAuthenticationServiceOption {
    /**
     * 存活时间
     */
    @Positive
    long ttl = 120L;
    /**
     * 落地页地址
     */
    @Nullable
    private String pageUrl;
    @NotBlank
    private String appId;
    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String appSecret;
}
