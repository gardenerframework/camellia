package io.gardenerframework.camellia.authentication.server.test.conf;

import io.gardenerframework.camellia.authentication.server.configuration.WeChatMiniProgramQrCodeAuthenticationServiceOption;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;

/**
 * @author zhanghan30
 * @date 2023/3/9 11:07
 */
@ConditionalOnClass(WeChatMiniProgramQrCodeAuthenticationServiceOption.class)
@Configuration
public class DemoWeChatUserAuthenticationServiceOptionConfigurer {
    public DemoWeChatUserAuthenticationServiceOptionConfigurer(WeChatMiniProgramQrCodeAuthenticationServiceOption option) throws IOException {
        WechatSecret secret = new Yaml().loadAs(
                new ClassPathResource(
                                "component/" +
                                "user-authentication-service/" +
                                "wechat/" +
                                "secret.yaml")
                        .getInputStream(),
                WechatSecret.class
        );
        option.setAppId(secret.getAppId());
        option.setAppSecret(secret.getAppSecret());
    }

    @Getter
    @Setter
    public static class WechatSecret {
        private String appId;
        private String appSecret;
    }
}
