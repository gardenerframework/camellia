package io.gardenerframework.camellia.authentication.server.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;

/**
 * @author zhanghan30
 * @date 2022/11/10 16:19
 */
@Configuration
@ConditionalOnClass(AlipayUserAuthenticationServiceOption.class)
public class DemoAlipayUserAuthenticationServiceOptionConfigurer {
    public DemoAlipayUserAuthenticationServiceOptionConfigurer(AlipayUserAuthenticationServiceOption alipayOption) throws IOException {
        AlipaySecret secret = new Yaml().loadAs(new ClassPathResource(
                        "authentication-server-demo/" +
                                "component/" +
                                "user-authentication-service/" +
                                "alipay/" +
                                "secret.yaml")
                        .getInputStream(),
                AlipaySecret.class
        );
        alipayOption.setAppId(secret.getAppId());
        alipayOption.setAliPublicKey(secret.getPublicKey());
        alipayOption.setPrivateKey(secret.getPrivateKey());
        alipayOption.setEncryptKey(secret.getEncryptKey());
    }

    @Getter
    @Setter
    public static class AlipaySecret {
        private String appId;
        private String publicKey;
        private String privateKey;
        private String encryptKey;
    }
}