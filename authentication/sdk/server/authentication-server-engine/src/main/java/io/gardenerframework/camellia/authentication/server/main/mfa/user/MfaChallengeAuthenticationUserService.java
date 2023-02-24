package io.gardenerframework.camellia.authentication.server.main.mfa.user;

import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.NestedAuthenticationException;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.credentials.PasswordCredentials;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.MfaChallengeIdPrincipal;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.UserService;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
import com.jdcloud.gardener.camellia.authorization.authentication.mfa.MfaAuthenticationChallengeResponseService;
import com.jdcloud.gardener.camellia.authorization.authentication.mfa.schema.MfaAuthenticationChallengeContext;
import com.jdcloud.gardener.camellia.authorization.challenge.ChallengeContextAccessor;
import com.jdcloud.gardener.camellia.authorization.challenge.exception.client.InvalidChallengeException;
import com.jdcloud.gardener.camellia.authorization.common.annotation.AuthorizationEnginePreserved;
import lombok.AllArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 支持mfa上下文的用户数据读取
 *
 * @author zhanghan30
 * @date 2022/5/12 8:46 下午
 */
@Component
@AllArgsConstructor
@AuthorizationEnginePreserved
public class MfaChallengeAuthenticationUserService implements UserService {
    private final ChallengeContextAccessor challengeContextAccessor;
    private final MfaAuthenticationChallengeResponseService service;

    @Nullable
    @Override
    public User authenticate(BasicPrincipal principal, PasswordCredentials credentials, Map<String, Object> context) throws AuthenticationException {
        return load(principal, context);
    }

    @Nullable
    @Override
    public User load(BasicPrincipal principal, Map<String, Object> context) throws AuthenticationException {
        if (principal instanceof MfaChallengeIdPrincipal) {
            String challengeId = principal.getName();
            MfaAuthenticationChallengeContext mfaAuthenticationChallengeContext = (MfaAuthenticationChallengeContext) challengeContextAccessor.getContext(service, challengeId);
            if (mfaAuthenticationChallengeContext == null) {
                throw new NestedAuthenticationException(new InvalidChallengeException(challengeId));
            }
            //将读取出来的上下文存储，防止被close后缓存消失导致无法重新读取
            context.put(MfaAuthenticationChallengeContext.class.getName(), mfaAuthenticationChallengeContext);
            return mfaAuthenticationChallengeContext.getRequest().getUser();
        } else {
            return null;
        }
    }
}
