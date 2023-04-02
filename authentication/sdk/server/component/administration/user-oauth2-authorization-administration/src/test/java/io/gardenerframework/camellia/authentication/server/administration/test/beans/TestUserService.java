package io.gardenerframework.camellia.authentication.server.administration.test.beans;

import io.gardenerframework.camellia.authentication.server.main.schema.subject.credentials.PasswordCredentials;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.user.UserService;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;

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
        return User.builder()
                .id(principal.getName())
                .principals(Collections.singletonList(principal))
                .name(principal.getName())
                .enabled(true)
                .locked(false)
                .build();
    }
}
