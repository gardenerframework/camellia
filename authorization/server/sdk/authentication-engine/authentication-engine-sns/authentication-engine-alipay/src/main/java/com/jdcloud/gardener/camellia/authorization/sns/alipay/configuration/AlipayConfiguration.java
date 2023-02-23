package com.jdcloud.gardener.camellia.authorization.sns.alipay.configuration;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import com.jdcloud.gardener.camellia.authorization.authentication.main.AlipayAuthenticationService;
import com.jdcloud.gardener.camellia.authorization.sns.alipay.AlipayPackage;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author zhanghan30
 * @date 2022/11/10 15:51
 */
@Configuration
@ComponentScan(basePackageClasses = AlipayPackage.class)
@Import(AlipayAuthenticationService.class)
public class AlipayConfiguration {
    public AlipayConfiguration(AlipayOption option) {
        Config config = new Config();
        config.protocol = "https";
        config.gatewayHost = "openapi.alipay.com";
        config.signType = "RSA2";
        config.appId = option.getAppId();
        config.merchantPrivateKey = option.getPrivateKey();
        config.encryptKey = option.getEncryptKey();
        config.alipayPublicKey = option.getAliPublicKey();
        Factory.setOptions(config);
    }
}
