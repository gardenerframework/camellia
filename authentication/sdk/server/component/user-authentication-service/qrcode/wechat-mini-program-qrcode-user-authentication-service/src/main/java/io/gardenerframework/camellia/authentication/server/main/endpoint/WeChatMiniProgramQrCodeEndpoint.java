package io.gardenerframework.camellia.authentication.server.main.endpoint;

import io.gardenerframework.camellia.authentication.server.configuration.WeChatMiniProgramQrCodeAuthenticationServiceOption;
import io.gardenerframework.camellia.authentication.server.main.qrcode.WeChatMiniProgramQrCodeService;
import io.gardenerframework.camellia.authentication.server.main.schema.request.CreateWeChatMiniProgramQrCodeRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhanghan30
 * @date 2023/3/16 19:02
 */
public abstract class WeChatMiniProgramQrCodeEndpoint
        <R extends CreateWeChatMiniProgramQrCodeRequest,
                S extends WeChatMiniProgramQrCodeService<R, ? extends WeChatMiniProgramQrCodeAuthenticationServiceOption>>
        extends QrCodeEndpoint<R, S> {
    public WeChatMiniProgramQrCodeEndpoint(S service) {
        super(service);
    }

    @Override
    protected boolean validateRequest(HttpServletRequest request) throws Exception {
        return true;
    }
}
