package io.gardenerframework.camellia.authentication.server.main.qrcode;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayOpenAppQrcodeCreateRequest;
import com.alipay.api.response.AlipayOpenAppQrcodeCreateResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gardenerframework.camellia.authentication.server.configuration.AlipayMiniProgramQrCodeAuthenticationServiceOption;
import io.gardenerframework.camellia.authentication.server.main.schema.request.CreateAlipayMiniProgramQrCodeRequest;
import io.gardenerframework.fragrans.data.cache.client.CacheClient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2023/3/16 18:34
 */
public abstract class AlipayMiniProgramQrCodeService<R extends CreateAlipayMiniProgramQrCodeRequest, O extends AlipayMiniProgramQrCodeAuthenticationServiceOption>
        extends QrCodeService<R> {
    @NonNull
    private final O option;

    private AlipayClient alipayClient;

    public AlipayMiniProgramQrCodeService(
            @NonNull CacheClient client,
            @NonNull O option
    ) {
        super(client);
        this.option = option;
    }

    @Override
    protected String generateCode(@Nullable R request) {
        //微信要求32个字符以内，所以就简单一些
        return UUID.randomUUID().toString().substring(0, 32);
    }

    @Override
    protected long getTtl() {
        return option.getTtl();
    }

    @Override
    protected String createImage(@NonNull R request, @NonNull String code) throws Exception {
        AlipayOpenAppQrcodeCreateRequest alipayOpenAppQrcodeCreateRequest = new AlipayOpenAppQrcodeCreateRequest();
        alipayOpenAppQrcodeCreateRequest.setBizContent(
                new ObjectMapper().writeValueAsString(
                        new AlipayQrCodeParam(
                                option.getPageUrl(),
                                String.format("code=%s", code),
                                code,
                                String.format("0x%08x", request.getColor()),
                                getQrCodeSize(request.getSize())
                        )
                ));
        AlipayOpenAppQrcodeCreateResponse response = createAlipayClient().execute(alipayOpenAppQrcodeCreateRequest);
        if (response.isSuccess()) {
            return response.getQrCodeUrlCircleWhite();
        } else {
            throw new IllegalStateException(response.getBody());
        }
    }

    private String getQrCodeSize(int metric) {
        if (metric <= 8) {
            return "s";
        }
        if (metric <= 12) {
            return "m";
        }
        return "l";
    }

    private synchronized AlipayClient createAlipayClient() {
        if (this.alipayClient == null
                && StringUtils.hasText(option.getAppId())
                && StringUtils.hasText(option.getEncryptKey())
                && StringUtils.hasText(option.getPrivateKey())
                && StringUtils.hasText(option.getAliPublicKey())) {
            this.alipayClient = new DefaultAlipayClient(
                    "https://openapi.alipay.com/gateway.do",
                    option.getAppId(),
                    option.getPrivateKey(),
                    "json",
                    "UTF-8",
                    option.getAliPublicKey(),
                    "RSA2"
            );
        }
        return this.alipayClient;
    }

    @AllArgsConstructor
    @Getter
    public static class AlipayQrCodeParam {
        @NonNull
        private final String url_param;
        @NonNull
        private final String query_param;
        @NonNull
        private final String describe;
        @NonNull
        private final String color;
        @NonNull
        private final String size;
    }
}
