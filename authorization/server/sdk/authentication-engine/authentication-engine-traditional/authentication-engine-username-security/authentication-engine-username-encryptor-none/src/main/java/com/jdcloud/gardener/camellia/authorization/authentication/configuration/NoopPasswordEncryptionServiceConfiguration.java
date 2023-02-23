package com.jdcloud.gardener.camellia.authorization.authentication.configuration;

import com.jdcloud.gardener.camellia.authorization.authentication.main.NoopPasswordEncryptionService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(NoopPasswordEncryptionService.class)
public class NoopPasswordEncryptionServiceConfiguration {
}
