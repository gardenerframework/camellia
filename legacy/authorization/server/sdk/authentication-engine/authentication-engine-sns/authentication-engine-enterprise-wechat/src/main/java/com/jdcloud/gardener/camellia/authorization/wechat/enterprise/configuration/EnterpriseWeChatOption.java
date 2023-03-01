package com.jdcloud.gardener.camellia.authorization.wechat.enterprise.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jdcloud.gardener.fragrans.api.options.schema.ApiOption;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;

/**
 * @author ZhangHan
 * @date 2022/11/8 20:58
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Component
@ApiOption(readonly = true)
public class EnterpriseWeChatOption {
    /**
     * 微信企业应用id
     */
    @NotBlank
    private String corpId;
    /**
     * 创建的app id
     */
    private String appId;
    /**
     * 微信企业应用密码
     * <p>
     * 需要创建应用和密码密码
     * <p>
     * 不显示给页面
     */
    @NotBlank
    @JsonIgnore
    private String appSecret;
}
