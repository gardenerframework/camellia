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
 * @date 2023/3/9 11:07
 */
@ConditionalOnClass(JdUserAuthenticationServiceOption.class)
@Configuration
public class DemoJdUserAuthenticationServiceOptionConfigurer {
    public DemoJdUserAuthenticationServiceOptionConfigurer(JdUserAuthenticationServiceOption option) throws IOException {
        JdSecret secret = new Yaml().loadAs(
                new ClassPathResource(
                        "authentication-server-demo/" +
                                "component/" +
                                "user-authentication-service/" +
                                "jd/" +
                                "secret.yaml")
                        .getInputStream(),
                JdSecret.class
        );
        option.setAppId(secret.getAppId());
        option.setAppSecret(secret.getAppSecret());
    }

    @Getter
    @Setter
    public static class JdSecret {
        private String appId;
        private String appSecret;
    }
}
