package io.gardenerframework.camellia.authentication.server.main.mfa.user;

import io.gardenerframework.camellia.authentication.infra.challenge.core.exception.ChallengeResponseServiceException;
import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEngineComponent;
import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEnginePreserved;
import io.gardenerframework.camellia.authentication.server.main.exception.NestedAuthenticationException;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.MfaAuthenticationChallengeResponseService;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.MfaAuthenticationScenario;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.MfaAuthenticationChallengeContext;
import io.gardenerframework.camellia.authentication.server.main.mfa.exception.client.BadMfaAuthenticationRequestException;
import io.gardenerframework.camellia.authentication.server.main.mfa.schema.principal.MfaAuthenticationPrincipal;
import io.gardenerframework.camellia.authentication.server.main.mfa.utils.MfaAuthenticationChallengeResponseServiceRegistry;
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
    private final MfaAuthenticationChallengeResponseServiceRegistry registry;

    @Nullable
    @Override
    public User authenticate(@NonNull Principal principal, @NonNull PasswordCredentials credentials, @Nullable Map<String, Object> context) throws AuthenticationException {
        return load(principal, context);
    }

    @Nullable
    @Override
    public User load(@NonNull Principal principal, @Nullable Map<String, Object> context) throws AuthenticationException {
        if (principal instanceof MfaAuthenticationPrincipal) {
            String challengeId = principal.getName();
            String authenticatorType = ((MfaAuthenticationPrincipal) principal).getAuthenticatorName();
            MfaAuthenticationChallengeResponseService service = Objects.requireNonNull(registry.getItem(authenticatorType)).getService();
            MfaAuthenticationChallengeContext mfaAuthenticationChallengeContext = null;
            try {
                mfaAuthenticationChallengeContext = service.getContext(
                        RequestingClientHolder.getClient(),
                        MfaAuthenticationScenario.class,
                        challengeId
                );
            } catch (ChallengeResponseServiceException e) {
                throw new NestedAuthenticationException(e);
            }
            if (mfaAuthenticationChallengeContext == null) {
                throw new NestedAuthenticationException(new BadMfaAuthenticationRequestException(challengeId));
            }
            return mfaAuthenticationChallengeContext.getUser();
        } else {
            //不是mfa的登录凭据，不进行读取
            return null;
        }
    }
}
