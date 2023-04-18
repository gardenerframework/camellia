package io.gardenerframework.camellia.authentication.server.main.endpoint;

import io.gardenerframework.camellia.authentication.server.configuration.AlipayMiniProgramQrCodeAuthenticationServiceOption;
import io.gardenerframework.camellia.authentication.server.main.qrcode.AlipayMiniProgramQrCodeService;
import io.gardenerframework.camellia.authentication.server.main.schema.request.CreateAlipayMiniProgramQrCodeRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhanghan30
 * @date 2023/3/16 19:02
 */
public abstract class AlipayMiniProgramQrCodeEndpoint<R extends CreateAlipayMiniProgramQrCodeRequest, S extends AlipayMiniProgramQrCodeService<R, ? extends AlipayMiniProgramQrCodeAuthenticationServiceOption>>
        extends QrCodeEndpoint<R, S> {
    public AlipayMiniProgramQrCodeEndpoint(S service) {
        super(service);
    }

    @Override
    protected boolean validateRequest(HttpServletRequest request) throws Exception {
        return true;
    }
}
