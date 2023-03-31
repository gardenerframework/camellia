package io.gardenerframework.camellia.authentication.server.configuration;

import io.gardenerframework.camellia.authentication.server.main.mfa.GenericMfaAuthenticatorClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = GenericMfaAuthenticatorClient.class)
public class DemoMfaConfiguration {
}
