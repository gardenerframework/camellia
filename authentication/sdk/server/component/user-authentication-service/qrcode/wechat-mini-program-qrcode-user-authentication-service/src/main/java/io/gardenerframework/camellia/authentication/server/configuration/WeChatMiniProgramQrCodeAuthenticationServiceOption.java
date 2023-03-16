package io.gardenerframework.camellia.authentication.server.configuration;

import io.gardenerframework.camellia.authentication.server.main.configuration.QrCodeBasedAuthenticationServiceOption;
import io.gardenerframework.fragrans.api.options.schema.ApiOption;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @author zhanghan30
 * @date 2023/3/16 18:23
 */
@ApiOption(readonly = false)
@Getter
@Setter
@WeChatMiniProgramQrCodeServiceComponent
public class WeChatMiniProgramQrCodeAuthenticationServiceOption extends QrCodeBasedAuthenticationServiceOption {
    @NotBlank
    private String appId;
    @NotBlank
    private String appSecret;
}
