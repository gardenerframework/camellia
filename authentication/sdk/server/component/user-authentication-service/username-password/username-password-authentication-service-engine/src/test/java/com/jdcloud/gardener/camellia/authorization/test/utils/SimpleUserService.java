package com.jdcloud.gardener.camellia.authorization.test.utils;

import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.credentials.PasswordCredentials;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.UserService;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

/**
 * @author ZhangHan
 * @date 2022/5/14 21:32
 */
@Component
public class SimpleUserService implements UserService {

    @Nullable
    @Override
    public User authenticate(BasicPrincipal principal, PasswordCredentials credentials, @Nullable Map<String, Object> context) throws AuthenticationException {
        return load(principal, context);
    }

    @Nullable
    @Override
    public User load(BasicPrincipal principal, @Nullable Map<String, Object> context) throws AuthenticationException {
        return new User(
                principal.getName(),
                null,
                Collections.singletonList(principal),
                false,
                true,
                null,
                null,
                "",
                null
        );
    }
}
