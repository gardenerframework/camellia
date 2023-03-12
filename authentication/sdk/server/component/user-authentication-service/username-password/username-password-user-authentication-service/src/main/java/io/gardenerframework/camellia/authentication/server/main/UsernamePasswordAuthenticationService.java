package io.gardenerframework.camellia.authentication.server.main;

import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.server.main.annotation.AuthenticationType;
import io.gardenerframework.camellia.authentication.server.main.schema.UserAuthenticationRequestToken;
import io.gardenerframework.camellia.authentication.server.main.schema.request.UsernamePasswordAuthenticationParameter;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.credentials.PasswordCredentials;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Validator;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author zhanghan30
 * @date 2021/12/27 12:44 下午
 */

@AuthenticationType("username")
@AllArgsConstructor
public class UsernamePasswordAuthenticationService implements UserAuthenticationService {
    private final Validator validator;
    @NonNull
    private final UsernameResolver resolver;
    @NonNull
    private final Collection<@NonNull BiConsumer<@NonNull HttpServletRequest, @NonNull UsernamePasswordAuthenticationParameter>> processors;

    @Override
    public UserAuthenticationRequestToken convert(@NonNull HttpServletRequest request, @Nullable OAuth2RequestingClient client, @NonNull Map<String, Object> context) throws AuthenticationException {
        UsernamePasswordAuthenticationParameter authenticationParameter = new UsernamePasswordAuthenticationParameter(request);
        authenticationParameter.validate(validator);
        if (!CollectionUtils.isEmpty(processors)) {
            processors.forEach(
                    processor -> processor.accept(request, authenticationParameter)
            );
            //消费完要重新验证
            authenticationParameter.validate(validator);
        }
        return new UserAuthenticationRequestToken(
                resolver.resolve(authenticationParameter.getUsername(), authenticationParameter.getPrincipalType()),
                //明确使用密码类型的凭据，要求走authenticate方法
                PasswordCredentials.builder().password(authenticationParameter.getPassword()).build()
        );
    }

    @Override
    public void authenticate(@NonNull UserAuthenticationRequestToken authenticationRequest, @Nullable OAuth2RequestingClient client, @NonNull User user, @NonNull Map<String, Object> context) throws AuthenticationException {
        if (!CollectionUtils.isEmpty(user.getCredentials())) {
            if (!user.getCredentials().contains(authenticationRequest.getCredentials())) {
                throw new BadCredentialsException("");
            }
        }
    }
}
