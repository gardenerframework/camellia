package io.gardenerframework.camellia.authentication.server.main.qrcode;

import io.gardenerframework.camellia.authentication.server.configuration.AppQrCodeAuthenticationServiceOption;
import io.gardenerframework.camellia.authentication.server.main.schema.request.CreateQrCodeRequest;
import io.gardenerframework.fragrans.data.cache.client.CacheClient;
import io.gardenerframework.fragrans.toolkits.barcode.QrCodeTool;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author zhanghan30
 * @date 2023/3/16 18:34
 */
public abstract class AppQrCodeService<C extends CreateQrCodeRequest, O extends AppQrCodeAuthenticationServiceOption> extends QrCodeService<C> {
    private final QrCodeTool qrCodeTool = new QrCodeTool();
    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final O option;

    /**
     * 基于编码和落地页获取url
     *
     * @param code 二维码编码
     * @return 落地页url
     * @throws Exception 遇到问题
     */
    protected abstract String buildPageFinalUrl(@NonNull String code) throws Exception;

    protected AppQrCodeService(@NonNull CacheClient client, @NonNull O option) {
        super(client);
        this.option = option;
    }

    @Override
    protected String createImage(@NonNull C request, @NonNull String code) throws Exception {
        return String.format("data:image/png;base64,%s", qrCodeTool.createSquareQrCode(
                buildPageFinalUrl(code),
                request.getSize(),
                option.getMargin(),
                option.getLogoPath(),
                option.getLogoRatio(),
                request.getColor(),
                -1
        ));
    }
}
