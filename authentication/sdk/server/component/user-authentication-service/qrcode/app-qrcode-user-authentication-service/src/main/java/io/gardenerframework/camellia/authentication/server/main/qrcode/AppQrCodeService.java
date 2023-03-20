package io.gardenerframework.camellia.authentication.server.main.qrcode;

import io.gardenerframework.camellia.authentication.server.configuration.AppQrCodeAuthenticationServiceOption;
import io.gardenerframework.camellia.authentication.server.main.schema.request.CreateQrCodeRequest;
import io.gardenerframework.fragrans.data.cache.client.CacheClient;
import io.gardenerframework.fragrans.toolkits.barcode.QrCodeTool;
import lombok.NonNull;

/**
 * @author zhanghan30
 * @date 2023/3/16 18:34
 */
public abstract class AppQrCodeService<C extends CreateQrCodeRequest, O extends AppQrCodeAuthenticationServiceOption> extends QrCodeService<C> {
    private final QrCodeTool qrCodeTool = new QrCodeTool();
    @NonNull
    private final O option;


    protected AppQrCodeService(@NonNull CacheClient client, @NonNull O option) {
        super(client);
        this.option = option;
    }

    @Override
    protected String createImage(@NonNull C request, @NonNull String code) throws Exception {
        return String.format("data:image/png;base64,%s", qrCodeTool.createSquareQrCode(
                code,
                request.getSize(),
                5,
                option.getLogoPath(),
                option.getLogoRatio(),
                request.getColor(),
                -1
        ));
    }
}
