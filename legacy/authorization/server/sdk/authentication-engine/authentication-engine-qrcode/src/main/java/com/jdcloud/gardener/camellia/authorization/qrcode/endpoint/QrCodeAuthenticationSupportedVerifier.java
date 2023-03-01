package com.jdcloud.gardener.camellia.authorization.qrcode.endpoint;

import com.jdcloud.gardener.camellia.authorization.qrcode.configuration.QrCodeOption;
import com.jdcloud.gardener.camellia.authorization.qrcode.exception.server.QrCodeAuthenticationNotSupportedException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author zhanghan30
 * @date 2021/12/24 6:47 下午
 */
@Aspect
@AllArgsConstructor
@Slf4j
@Component
public class QrCodeAuthenticationSupportedVerifier {
    private final QrCodeOption qrCodeOption;

    /**
     * 断点切点
     */
    @Before("within(com.jdcloud.gardener.camellia.authorization.qrcode.endpoint.QrCodeApiEndpoint)")
    public void check() {
        if (!StringUtils.hasText(qrCodeOption.getUrl())) {
            throw new QrCodeAuthenticationNotSupportedException();
        }
    }
}
