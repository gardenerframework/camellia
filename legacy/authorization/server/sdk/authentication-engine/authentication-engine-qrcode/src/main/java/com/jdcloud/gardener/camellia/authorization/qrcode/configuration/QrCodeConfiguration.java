package com.jdcloud.gardener.camellia.authorization.qrcode.configuration;

import com.jdcloud.gardener.camellia.authorization.authentication.main.QrCodeAuthenticationService;
import com.jdcloud.gardener.camellia.authorization.qrcode.QrCodePackage;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author zhanghan30
 * @date 2021/12/23 12:19 上午
 */
@Configuration
@ComponentScan(basePackageClasses = QrCodePackage.class)
@Import(QrCodeAuthenticationService.class)
public class QrCodeConfiguration {
}
