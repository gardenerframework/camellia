package com.jdcloud.gardener.camellia.authorization.cas.configuration;

import com.jdcloud.gardener.fragrans.api.options.schema.ApiOption;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;

/**
 * @author zhanghan30
 * @date 2022/8/23 4:37 下午
 */
@ApiOption(readonly = true)
@Component
@Getter
@Setter
public class CasOption {
    /**
     * 认证页面地址
     */
    @NotBlank
    private String authenticationPageUrl;
    /**
     * 票据验证服务地址
     */
    @NotBlank
    private String ticketValidationUrl;
    /**
     * 向cas中心注册的回调地址
     */
    @NotBlank
    private String callbackUrl;
}
