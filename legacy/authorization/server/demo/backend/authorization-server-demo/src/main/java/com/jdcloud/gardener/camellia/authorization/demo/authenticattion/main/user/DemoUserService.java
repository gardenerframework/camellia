package com.jdcloud.gardener.camellia.authorization.demo.authenticattion.main.user;

import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.credentials.PasswordCredentials;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.UserService;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
import lombok.Setter;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author ZhangHan
 * @date 2022/5/15 0:43
 */
@Component
public class DemoUserService implements UserService {
    @Setter
    private String password = "123456";

    @Nullable
    @Override
    public User authenticate(BasicPrincipal principal, PasswordCredentials credentials, @Nullable Map<String, Object> context) throws AuthenticationException {
        if (Objects.equals(credentials.getPassword(), password)) {
            return load(principal, context);
        } else {
            throw new BadCredentialsException(principal.getName());
        }
    }

    @Nullable
    @Override
    public User load(BasicPrincipal principal, @Nullable Map<String, Object> context) throws AuthenticationException, UnsupportedOperationException {
        return new User(
                principal.getName(),
                new PasswordCredentials(password),
                Collections.singletonList(principal),
                principal.getName().equals("locked"),
                !principal.getName().equals("disabled"),
                null,
                null,
                principal.getName(),
                "https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg"
        );
    }
}
