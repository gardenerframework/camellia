package io.gardenerframework.camellia.authentication.server.test.conf;

import io.gardenerframework.camellia.authentication.server.configuration.AlipayMiniProgramQrCodeAuthenticationServiceOption;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;

/**
 * @author zhanghan30
 * @date 2023/3/9 11:07
 */
@Configuration
public class AlipayUserAuthenticationServiceOptionConfigurer {
    public AlipayUserAuthenticationServiceOptionConfigurer(AlipayMiniProgramQrCodeAuthenticationServiceOption option) throws IOException {
        AlipaySecret secret = new Yaml().loadAs(
                new ClassPathResource(
                        "component/" +
                                "user-authentication-service/" +
                                "alipay/" +
                                "secret.yaml")
                        .getInputStream(),
                AlipaySecret.class
        );
        option.setAppId(secret.getAppId());
        option.setAliPublicKey(secret.getAliPublicKey());
        option.setEncryptKey(secret.encryptKey);
        option.setPrivateKey(secret.getPrivateKey());
    }

    @Getter
    @Setter
    public static class AlipaySecret {
        /**
         * 应用id
         * <p>
         * 只读
         */
        private String appId;
        /**
         * 应用私钥(不对外展示)
         */
        private String privateKey;
        /**
         * 商家的aes加密密钥
         */
        private String encryptKey;
        /**
         * 阿里的公钥
         */
        private String aliPublicKey;
    }
}
