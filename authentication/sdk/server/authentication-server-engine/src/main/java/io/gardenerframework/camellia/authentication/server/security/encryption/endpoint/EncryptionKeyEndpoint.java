package io.gardenerframework.camellia.authentication.server.security.encryption.endpoint;

import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEngineComponent;
import io.gardenerframework.camellia.authentication.server.common.api.group.AuthenticationServerRestController;
import io.gardenerframework.camellia.authentication.server.security.encryption.EncryptionService;
import io.gardenerframework.camellia.authentication.server.security.encryption.schema.EncryptionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Duration;

@AuthenticationServerRestController
@AuthenticationServerEngineComponent
@RequestMapping("/security/encryption/key")
@RequiredArgsConstructor
@ConditionalOnBean(EncryptionService.class)
public class EncryptionKeyEndpoint {
    private final EncryptionService encryptionService;

    @PostMapping
    EncryptionKey createKey() throws Exception {
        //默认1小时有效
        return encryptionService.createKey(Duration.ofSeconds(3600));
    }
}
