package com.jdcloud.gardener.camellia.authorization.authentication.configuration;


import com.jdcloud.gardener.camellia.authorization.authentication.main.DesPasswordEncryptionService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(DesPasswordEncryptionService.class)
public class DesPasswordEncryptionServiceConfiguration {
}
