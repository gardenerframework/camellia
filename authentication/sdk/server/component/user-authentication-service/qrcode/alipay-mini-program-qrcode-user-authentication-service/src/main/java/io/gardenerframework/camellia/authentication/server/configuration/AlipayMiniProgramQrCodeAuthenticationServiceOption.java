package io.gardenerframework.camellia.authentication.server.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.gardenerframework.fragrans.api.options.schema.ApiOption;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
public abstract class AlipayMiniProgramQrCodeAuthenticationServiceOption {
    /**
     * 存活时间
     */
    @Positive
    long ttl = 120L;
    /**
     * 落地页地址
     */
    @NotBlank
    private String pageUrl;
    /**
     * 应用id
     * <p>
     * 只读
     */
    @NotBlank
    private String appId;
    /**
     * 应用私钥(不对外展示)
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    private String privateKey;
    /**
     * 商家的aes加密密钥
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    private String encryptKey;
    /**
     * 阿里的公钥
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    private String aliPublicKey;
}
