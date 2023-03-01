package com.jdcloud.gardener.camellia.authorization.test.security;

import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.credentials.PasswordCredentials;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.UserService;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
import com.jdcloud.gardener.camellia.authorization.test.security.authentication.TestUserPrincipal;
import com.jdcloud.gardener.camellia.authorization.test.security.authentication.principal.AccountExpiredPrincipal;
import com.jdcloud.gardener.camellia.authorization.test.security.authentication.principal.DisabledPrincipal;
import com.jdcloud.gardener.camellia.authorization.test.security.authentication.principal.LockedPrincipal;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * @author ZhangHan
 * @date 2022/4/23 1:53
 */
@Component
public class TestUserService implements UserService {

    @Override
    public User authenticate(BasicPrincipal principal, PasswordCredentials credentials, Map<String, Object> context) throws AuthenticationException {
        if (!StringUtils.hasText(credentials.getPassword())) {
            throw new BadCredentialsException(principal.getName());
        }
        return load(principal, context);
    }

    @Override
    public User load(BasicPrincipal principal, Map<String, Object> context) throws AuthenticationException {
        return new TestUserPrincipal(
                principal.getName(),
                null,
                Collections.singletonList(principal),
                principal instanceof LockedPrincipal,
                !(principal instanceof DisabledPrincipal),
                null,
                principal instanceof AccountExpiredPrincipal ? Date.from(Instant.now().minusSeconds(100)) : null,
                principal.getName()
        );
    }
}
