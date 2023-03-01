package com.jdcloud.gardener.camellia.authorization.test.utils;

import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.UsernamePrincipal;
import com.jdcloud.gardener.camellia.authorization.username.UsernameResolver;
import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * @author ZhangHan
 * @date 2022/5/14 21:18
 */
@Component
public class SimpleUsernameResolver implements UsernameResolver {
    @Override
    public BasicPrincipal resolve(String username, @Nullable String principalType) throws AuthenticationException {
        return new UsernamePrincipal(username);
    }
}
