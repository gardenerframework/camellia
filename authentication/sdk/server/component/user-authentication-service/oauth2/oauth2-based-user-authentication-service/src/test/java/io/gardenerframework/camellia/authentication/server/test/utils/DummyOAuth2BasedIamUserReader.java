package io.gardenerframework.camellia.authentication.server.test.utils;

import io.gardenerframework.camellia.authentication.server.main.OAuth2BasedIamUserReader;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.UsernamePrincipal;
import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * @author zhanghan30
 * @date 2023/3/6 19:14
 */
@Component
public class DummyOAuth2BasedIamUserReader implements OAuth2BasedIamUserReader {
    @Nullable
    @Override
    public Principal readUser(@NonNull String code) throws Exception {
        return UsernamePrincipal.builder().name(code).build();
    }
}
