package io.gardenerframework.camellia.authentication.server.main;

import io.gardenerframework.camellia.authentication.server.main.qrcode.WeChatMiniProgramQrCodeService;
import lombok.NonNull;

import javax.validation.Validator;

/**
 * @author zhanghan30
 * @date 2023/3/16 18:23
 */
@SuppressWarnings("rawtypes")
public abstract class WeChatMiniProgramQrCodeAuthenticationService
        <S extends WeChatMiniProgramQrCodeService>
        extends QrCodeBasedAuthenticationService<S> {
    public WeChatMiniProgramQrCodeAuthenticationService(@NonNull Validator validator, @NonNull S service) {
        super(validator, service);
    }
}