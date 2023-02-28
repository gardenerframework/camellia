package com.jdcloud.gardener.camellia.authorization.test.security;

import com.jdcloud.gardener.camellia.authorization.test.security.authentication.TestUserPrincipal;
import com.jdcloud.gardener.camellia.authorization.test.security.authentication.principal.AccountExpiredPrincipal;
import com.jdcloud.gardener.camellia.authorization.test.security.authentication.principal.DisabledPrincipal;
import com.jdcloud.gardener.camellia.authorization.test.security.authentication.principal.LockedPrincipal;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.credentials.PasswordCredentials;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.user.UserService;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * @author ZhangHan
 * @date 2022/4/23 1:53
 */
@Component
public class TestUserService implements UserService {

    @Override
    public User authenticate(Principal principal, PasswordCredentials credentials, Map<String, Object> context) throws AuthenticationException {
        if (!StringUtils.hasText(credentials.getPassword())) {
            throw new BadCredentialsException(principal.getName());
        }
        return load(principal, context);
    }

    @Override
    public User load(Principal principal, Map<String, Object> context) throws AuthenticationException {
        return TestUserPrincipal.builder()
                .id(UUID.randomUUID().toString())
                .username(principal.getName())
                .credentials(null)
                .principal(principal)
                .enabled(!(principal instanceof DisabledPrincipal))
                .locked(principal instanceof LockedPrincipal)
                .subjectExpiryDate(principal instanceof AccountExpiredPrincipal ? Date.from(Instant.now().minusSeconds(100)) : null)
                .name(principal.getName()).build();
    }
}
