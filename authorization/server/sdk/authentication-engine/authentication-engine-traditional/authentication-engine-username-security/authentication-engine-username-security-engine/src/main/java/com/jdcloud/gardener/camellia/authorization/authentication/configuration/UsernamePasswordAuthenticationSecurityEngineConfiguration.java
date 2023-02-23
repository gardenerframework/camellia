package com.jdcloud.gardener.camellia.authorization.authentication.configuration;

import com.jdcloud.gardener.camellia.authorization.authentication.main.PasswordParameterEncryptionPostProcessor;
import com.jdcloud.gardener.camellia.authorization.authentication.main.endpoint.PasswordEncryptionKeyEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author zhanghan30
 * @date 2021/12/31 7:33 下午
 */
@Configuration
@Slf4j
@Import({UsernamePasswordAuthenticationSecurityOption.class, PasswordEncryptionKeyEndpoint.class, PasswordParameterEncryptionPostProcessor.class})
public class UsernamePasswordAuthenticationSecurityEngineConfiguration {
}
