package io.gardenerframework.camellia.authentication.server.security.encryption.endpoint;

import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEngineComponent;
import io.gardenerframework.camellia.authentication.server.common.api.group.AuthenticationServerRestController;
import io.gardenerframework.camellia.authentication.server.security.encryption.EncryptionService;
import io.gardenerframework.camellia.authentication.server.security.encryption.schema.EncryptionKey;
import io.gardenerframework.fragrans.api.standard.error.exception.server.NotImplementedException;
import lombok.AllArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Duration;
import java.util.Collection;

@AuthenticationServerRestController
@AuthenticationServerEngineComponent
@RequestMapping("/security/encryption/key")
@AllArgsConstructor
public class EncryptionKeyEndpoint {
    private final Collection<EncryptionService> encryptionServices;

    @PostMapping
    EncryptionKey createKey() throws Exception {
        //默认1小时有效
        if (!CollectionUtils.isEmpty(encryptionServices)) {
            return encryptionServices.toArray(new EncryptionService[]{})[0].createKey(Duration.ofSeconds(3600));
        }
        throw new NotImplementedException();
    }
}
