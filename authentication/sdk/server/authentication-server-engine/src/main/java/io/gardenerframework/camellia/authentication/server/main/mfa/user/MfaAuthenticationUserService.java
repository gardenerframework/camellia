package io.gardenerframework.camellia.authentication.server.main.mfa.user;

import io.gardenerframework.camellia.authentication.infra.challenge.core.exception.ChallengeResponseServiceException;
import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEngineComponent;
import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEnginePreserved;
import io.gardenerframework.camellia.authentication.server.main.exception.NestedAuthenticationException;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.AuthenticationServerMfaAuthenticationChallengeResponseService;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.AuthenticationServerMfaChallengeContext;
import io.gardenerframework.camellia.authentication.server.main.mfa.exception.client.BadMfaRequestException;
import io.gardenerframework.camellia.authentication.server.main.mfa.schema.principal.MfaPrincipal;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.credentials.PasswordCredentials;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.user.UserService;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import io.gardenerframework.camellia.authentication.server.main.utils.RequestingClientHolder;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;

import java.util.Map;
import java.util.Objects;

/**
 * 支持mfa上下文的用户数据读取
 *
 * @author zhanghan30
 * @date 2022/5/12 8:46 下午
 */
@AllArgsConstructor
@AuthenticationServerEnginePreserved
@AuthenticationServerEngineComponent
public class MfaAuthenticationUserService implements UserService {
    private final AuthenticationServerMfaAuthenticationChallengeResponseService authenticationServerMfaAuthenticationChallengeResponseService;

    @Nullable
    @Override
    public User authenticate(@NonNull Principal principal, @NonNull PasswordCredentials credentials, @Nullable Map<String, Object> context) throws AuthenticationException {
        return load(principal, context);
    }

    @Nullable
    @Override
    public User load(@NonNull Principal principal, @Nullable Map<String, Object> context) throws AuthenticationException {
        if (principal instanceof MfaPrincipal) {
            String challengeId = principal.getName();
            AuthenticationServerMfaChallengeContext authenticationServerMfaChallengeContext = null;
            try {
                authenticationServerMfaChallengeContext = authenticationServerMfaAuthenticationChallengeResponseService.getContext(
                        RequestingClientHolder.getClient(),
                        authenticationServerMfaAuthenticationChallengeResponseService.getClass(),
                        challengeId
                );
                Objects.requireNonNull(context).put(AuthenticationServerMfaChallengeContext.class.getName(), authenticationServerMfaChallengeContext);
            } catch (ChallengeResponseServiceException e) {
                throw new NestedAuthenticationException(e);
            }
            if (authenticationServerMfaChallengeContext == null) {
                throw new BadMfaRequestException(challengeId);
            }
            return authenticationServerMfaChallengeContext.getUser();
        } else {
            //不是mfa的登录凭据，不进行读取
            return null;
        }
    }
}
