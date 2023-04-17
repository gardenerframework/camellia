package io.gardenerframework.camellia.authentication.server.configuration;

import io.gardenerframework.camellia.authentication.server.main.WeChatMiniProgramQrCodeAuthenticationService;
import io.gardenerframework.camellia.authentication.server.main.endpoint.WeChatMiniProgramQrCodeEndpoint;
import io.gardenerframework.camellia.authentication.server.main.qrcode.WeChatMiniProgramQrCodeService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhanghan30
 * @date 2023/4/17 15:40
 */
@Configuration
@AllArgsConstructor
@SuppressWarnings("rawtypes")
public class WeChatMiniProgramQrCodeAuthenticationServiceConfiguration {
    @NonNull
    private final WeChatMiniProgramQrCodeAuthenticationServiceOption weChatMiniProgramQrCodeAuthenticationServiceOption;
    @NonNull
    private final WeChatMiniProgramQrCodeEndpoint weChatMiniProgramQrCodeEndpoint;
    @NonNull
    private final WeChatMiniProgramQrCodeService weChatMiniProgramQrCodeService;
    @NonNull
    private final WeChatMiniProgramQrCodeAuthenticationService weChatMiniProgramQrCodeAuthenticationService;
}
