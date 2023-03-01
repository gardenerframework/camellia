package com.jdcloud.gardener.camellia.authorization.demo.qrcode.configuration;

import com.jdcloud.gardener.camellia.authorization.qrcode.configuration.QrCodeOption;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

/**
 * @author zhanghan30
 * @date 2022/8/9 2:17 下午
 */
@Component
@ConditionalOnClass(QrCodeOption.class)
public class QrCodeOptionCustomizer {
    public QrCodeOptionCustomizer(QrCodeOption qrCodeOption) {
        qrCodeOption.setUrl("http://localhost");
    }
}
