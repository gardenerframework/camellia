package io.gardenerframework.camellia.authentication.server.main.user;

import io.gardenerframework.camellia.authentication.server.main.schema.subject.credentials.PasswordCredentials;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
public class DemoUserService implements UserService {
    private final String password = "123456";

    @Nullable
    @Override
    public User authenticate(@NonNull Principal principal, @NonNull PasswordCredentials credentials, @Nullable Map<String, Object> context) throws AuthenticationException {
        if (Objects.equals(credentials.getPassword(), password)) {
            return load(principal, context);
        } else {
            throw new BadCredentialsException(principal.getName());
        }
    }

    @Nullable
    @Override
    public User load(@NonNull Principal principal, @Nullable Map<String, Object> context) throws AuthenticationException, UnsupportedOperationException {
        return User.builder()
                .id(principal.getName())
                .credential(PasswordCredentials.builder().password(password).build())
                .name(principal.getName())
                .avatar("https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg")
                .enabled(!principal.getName().equals("disabled"))
                .locked(principal.getName().equals("locked"))
                .principal(principal)
                .build();
    }
}
