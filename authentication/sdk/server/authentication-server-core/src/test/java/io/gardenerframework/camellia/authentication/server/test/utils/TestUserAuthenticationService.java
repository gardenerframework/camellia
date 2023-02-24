package io.gardenerframework.camellia.authentication.server.test.utils;

import io.gardenerframework.camellia.authentication.server.main.AbstractUserAuthenticationService;
import io.gardenerframework.camellia.authentication.server.main.schema.UserAuthenticationRequestToken;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.UsernamePrincipal;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Validator;
import java.util.UUID;

/**
 * @author zhanghan30
 * @date 2023/2/17 16:37
 */
@Component
public class TestUserAuthenticationService extends AbstractUserAuthenticationService<UsernamePasswordAuthenticationParameter> {
    @Getter
    @Setter
    private boolean nullUsername = true;

    public TestUserAuthenticationService(@NonNull Validator validator) {
        super(validator);
    }

    @Override
    protected UsernamePasswordAuthenticationParameter getAuthenticationParameter(@NonNull HttpServletRequest request) {
        UsernamePasswordAuthenticationParameter usernamePasswordAuthenticationParameter = new UsernamePasswordAuthenticationParameter();
        if (!nullUsername) {
            usernamePasswordAuthenticationParameter.setUsername(UUID.randomUUID().toString());
            usernamePasswordAuthenticationParameter.setPassword(UUID.randomUUID().toString());
        }
        return usernamePasswordAuthenticationParameter;
    }

    @Override
    protected UserAuthenticationRequestToken doConvert(@NonNull UsernamePasswordAuthenticationParameter authenticationParameter) {
        return new UserAuthenticationRequestToken(UsernamePrincipal.builder()
                .name(authenticationParameter.getUsername())
                .build());
    }

    @Override
    public void authenticate(@NonNull UserAuthenticationRequestToken authenticationRequest, @NonNull User user) throws AuthenticationException {

    }
}
