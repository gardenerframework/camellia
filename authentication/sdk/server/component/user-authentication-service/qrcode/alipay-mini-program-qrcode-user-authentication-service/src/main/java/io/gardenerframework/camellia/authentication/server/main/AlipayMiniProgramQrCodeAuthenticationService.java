package io.gardenerframework.camellia.authentication.server.main;

import io.gardenerframework.camellia.authentication.server.main.qrcode.AlipayMiniProgramQrCodeService;
import lombok.NonNull;

import javax.validation.Validator;

/**
 * @author zhanghan30
 * @date 2023/3/16 18:23
 */
@SuppressWarnings("rawtypes")
public abstract class AlipayMiniProgramQrCodeAuthenticationService
        <S extends AlipayMiniProgramQrCodeService>
        extends QrCodeBasedAuthenticationService<S> {
    public AlipayMiniProgramQrCodeAuthenticationService(@NonNull Validator validator, @NonNull S service) {
        super(validator, service);
    }
}