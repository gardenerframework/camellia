package io.gardenerframework.camellia.authentication.server.test.utils;

import io.gardenerframework.camellia.authentication.server.main.UserAuthenticationService;
import io.gardenerframework.camellia.authentication.server.main.utils.UserAuthenticationServiceRegistry;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

/**
 * @author zhanghan30
 * @date 2023/3/6 19:03
 */
@Component
@AllArgsConstructor
public class DummyUserAuthenticationServiceRegistry implements UserAuthenticationServiceRegistry {
    private final TestOAuth2BaseUserAuthenticationService testOAuth2BaseUserAuthenticationService;

    @Override
    public Collection<String> getUserAuthenticationServiceTypes(boolean ignorePreserved) {
        return Collections.singletonList("test");
    }

    @Override
    public boolean hasUserAuthenticationService(@NonNull String type, boolean ignorePreserved) {
        return "test".equals(type);
    }

    @Nullable
    @Override
    public UserAuthenticationService getUserAuthenticationService(@NonNull String type, boolean ignorePreserved) {
        return type.equals("test") ? testOAuth2BaseUserAuthenticationService : null;
    }
}
