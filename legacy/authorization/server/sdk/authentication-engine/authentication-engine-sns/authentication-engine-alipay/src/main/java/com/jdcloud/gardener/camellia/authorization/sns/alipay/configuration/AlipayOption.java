package com.jdcloud.gardener.camellia.authorization.sns.alipay.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jdcloud.gardener.fragrans.api.options.schema.ApiOption;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;

/**
 * @author zhanghan30
 * @date 2022/11/10 15:28
 */
@ApiOption(readonly = true)
@Getter
@Setter
@NoArgsConstructor
@Component
public class AlipayOption {
    /**
     * 应用id
     * <p>
     * 只读
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @NotBlank
    private String appId;
    /**
     * 应用私钥(不对外展示)
     */
    @JsonIgnore
    @NotBlank
    private String privateKey;
    /**
     * 商家的aes加密密钥
     */
    @JsonIgnore
    @NotBlank
    private String encryptKey;
    /**
     * 阿里的公钥
     */
    @JsonIgnore
    @NotBlank
    private String aliPublicKey;
}
