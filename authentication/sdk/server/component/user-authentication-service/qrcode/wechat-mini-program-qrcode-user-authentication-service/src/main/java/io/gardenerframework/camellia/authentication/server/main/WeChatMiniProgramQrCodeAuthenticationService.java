package io.gardenerframework.camellia.authentication.server.main;

import io.gardenerframework.camellia.authentication.server.configuration.WeChatMiniProgramQrCodeServiceComponent;
import io.gardenerframework.camellia.authentication.server.main.annotation.AuthenticationType;
import io.gardenerframework.camellia.authentication.server.main.qrcode.WeChatMiniProgramQrCodeService;
import lombok.NonNull;

import javax.validation.Validator;

/**
 * @author zhanghan30
 * @date 2023/3/16 18:23
 */
@AuthenticationType("qrcode.wechat-mini")
@WeChatMiniProgramQrCodeServiceComponent
public class WeChatMiniProgramQrCodeAuthenticationService extends QrCodeBasedAuthenticationService<WeChatMiniProgramQrCodeService> {
    public WeChatMiniProgramQrCodeAuthenticationService(@NonNull Validator validator, @NonNull WeChatMiniProgramQrCodeService service) {
        super(validator, service);
    }
}
