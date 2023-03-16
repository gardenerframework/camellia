package io.gardenerframework.camellia.authentication.server.main.endpoint;

import io.gardenerframework.camellia.authentication.server.configuration.WeChatMiniProgramQrCodeServiceComponent;
import io.gardenerframework.camellia.authentication.server.main.configuration.QrCodeBasedAuthenticationServiceOption;
import io.gardenerframework.camellia.authentication.server.main.qrcode.QrCodeService;
import io.gardenerframework.camellia.authentication.server.main.schema.request.CreateWeChatMiniProgramQrCodeRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhanghan30
 * @date 2023/3/16 19:02
 */
@WeChatMiniProgramQrCodeServiceComponent
public class WeChatMiniProgramQrCodeEndpoint extends QrCodeEndpoint<CreateWeChatMiniProgramQrCodeRequest> {
    public WeChatMiniProgramQrCodeEndpoint(QrCodeService<CreateWeChatMiniProgramQrCodeRequest, ? extends QrCodeBasedAuthenticationServiceOption> service) {
        super(service);
    }

    @Override
    protected boolean validateRequest(HttpServletRequest request) throws Exception {
        return true;
    }
}
