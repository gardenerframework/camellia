package com.jdcloud.gardener.camellia.authorization.authentication.main.endpoint;

import com.jdcloud.gardener.camellia.authorization.authentication.configuration.UsernamePasswordAuthenticationSecurityOption;
import com.jdcloud.gardener.camellia.authorization.authentication.main.PasswordEncryptionKeyStore;
import com.jdcloud.gardener.camellia.authorization.authentication.main.PasswordEncryptionService;
import com.jdcloud.gardener.camellia.authorization.authentication.main.annotation.EncryptionAlgorithm;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.response.CreateDynamicEncryptionKeyResponse;
import com.jdcloud.gardener.camellia.authorization.common.api.group.AuthorizationServerRestController;
import com.jdcloud.gardener.camellia.authorization.common.api.security.AccessTokenProtectedEndpoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.Duration;
import java.util.Objects;

@AuthorizationServerRestController
@Slf4j
@AccessTokenProtectedEndpoint(optional = true)
@RequestMapping("/authentication/username/key")
@RequiredArgsConstructor
public class PasswordEncryptionKeyEndpoint {
    private final PasswordEncryptionService encryptionService;
    private final PasswordEncryptionKeyStore keyStore;
    private final UsernamePasswordAuthenticationSecurityOption securityOption;

    @PostMapping
    public CreateDynamicEncryptionKeyResponse createKey(HttpServletRequest httpServletRequest) throws Exception {
        HttpSession session = httpServletRequest.getSession(true);
        //获取session id
        String sessionId = session.getId();
        String key = keyStore.load(sessionId);
        Long ttl = keyStore.getTtl(sessionId);
        if (key == null || ttl == null) {
            key = encryptionService.generateKey();
            ttl = securityOption.getEncryptionKeyTtl();
            keyStore.save(sessionId, key, Duration.ofSeconds(ttl));
        }
        return new CreateDynamicEncryptionKeyResponse(
                securityOption.isShowAlgorithm() ?
                        Objects.requireNonNull(AnnotationUtils.findAnnotation(encryptionService.getClass(), EncryptionAlgorithm.class)).value() : null,
                key,
                ttl
        );
    }
}
