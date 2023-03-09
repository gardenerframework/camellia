package io.gardenerframework.camellia.authentication.server.main;

import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.server.main.annotation.AuthenticationType;
import io.gardenerframework.camellia.authentication.server.main.schema.UserAuthenticationRequestToken;
import io.gardenerframework.camellia.authentication.server.main.schema.request.UsernamePasswordAuthenticationParameter;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.credentials.PasswordCredentials;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Validator;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author zhanghan30
 * @date 2021/12/27 12:44 下午
 */

@AuthenticationType("username")
public class UsernamePasswordAuthenticationService extends AbstractUserAuthenticationService<UsernamePasswordAuthenticationParameter> {
    @NonNull
    private final UsernameResolver resolver;
    @NonNull
    private final Collection<@NonNull Consumer<@NonNull UsernamePasswordAuthenticationParameter>> processors;

    public UsernamePasswordAuthenticationService(@NonNull Validator validator, @NonNull UsernameResolver resolver, @NonNull Collection<@NonNull Consumer<@NonNull UsernamePasswordAuthenticationParameter>> processors) {
        super(validator);
        this.resolver = resolver;
        this.processors = processors;
    }

    @Override
    protected UsernamePasswordAuthenticationParameter getAuthenticationParameter(@NonNull HttpServletRequest request) {
        return new UsernamePasswordAuthenticationParameter(request);
    }

    @Override
    protected UserAuthenticationRequestToken doConvert(@NonNull UsernamePasswordAuthenticationParameter authenticationParameter, @Nullable OAuth2RequestingClient client, @NonNull Map<String, Object> context) throws Exception {
        if (!CollectionUtils.isEmpty(processors)) {
            processors.forEach(
                    processor -> processor.accept(authenticationParameter)
            );
            //消费完要重新验证
            authenticationParameter.validate(this.getValidator());
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
