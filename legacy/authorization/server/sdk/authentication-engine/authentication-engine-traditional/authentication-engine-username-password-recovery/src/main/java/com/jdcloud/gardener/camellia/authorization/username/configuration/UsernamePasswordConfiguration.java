package com.jdcloud.gardener.camellia.authorization.username.configuration;

import com.jdcloud.gardener.camellia.authorization.username.recovery.endpoint.PasswordRecoveryEndpoint;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author ZhangHan
 * @date 2022/5/14 19:46
 */
@Configuration
@Import({
        PasswordRecoveryEndpoint.class
})
public class UsernamePasswordConfiguration {
}
