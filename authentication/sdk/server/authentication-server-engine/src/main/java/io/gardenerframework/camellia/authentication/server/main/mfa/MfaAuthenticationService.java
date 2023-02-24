package io.gardenerframework.camellia.authentication.server.main.mfa;

import com.jdcloud.gardener.camellia.authorization.authentication.main.UserAuthenticationService;
import com.jdcloud.gardener.camellia.authorization.authentication.main.annotation.AuthenticationType;
import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.NestedAuthenticationException;
import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.client.BadAuthenticationRequestParameterException;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.UserAuthenticationRequestToken;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.MfaChallengeIdPrincipal;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
import com.jdcloud.gardener.camellia.authorization.authentication.mfa.schema.credentials.MfaResponseCredentials;
import com.jdcloud.gardener.camellia.authorization.authentication.mfa.schema.request.MfaResponseParameter;
import com.jdcloud.gardener.camellia.authorization.challenge.exception.client.BadResponseException;
import com.jdcloud.gardener.camellia.authorization.challenge.exception.client.ChallengeException;
import com.jdcloud.gardener.camellia.authorization.common.annotation.AuthorizationEnginePreserved;
import lombok.AllArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

/**
 * @author zhanghan30
 * @date 2022/5/12 9:09 下午
 */
@Component
@AuthorizationEnginePreserved
@AuthenticationType(value = "mfa")
@AllArgsConstructor
public class MfaAuthenticationService implements UserAuthenticationService {
    private final Validator validator;
    private final MfaAuthenticationChallengeResponseService mfaAuthenticationChallengeResponseService;

    @Override
    public UserAuthenticationRequestToken convert(HttpServletRequest request) throws AuthenticationException {
        MfaResponseParameter mfaResponseParameter = new MfaResponseParameter(request);
        Set<ConstraintViolation<Object>> violations = validator.validate(mfaResponseParameter);
        if (!CollectionUtils.isEmpty(violations)) {
            throw new BadAuthenticationRequestParameterException(violations);
        }
        return new UserAuthenticationRequestToken(
                new MfaChallengeIdPrincipal(mfaResponseParameter.getChallengeId()),
                new MfaResponseCredentials(mfaResponseParameter.getResponse())
        );
    }

    @Override
    public void authenticate(UserAuthenticationRequestToken authenticationRequest, User user) throws AuthenticationException {
        MfaChallengeIdPrincipal principal = (MfaChallengeIdPrincipal) authenticationRequest.getPrincipal();
        MfaResponseCredentials credential = (MfaResponseCredentials) authenticationRequest.getCredentials();
        try {
            if (!mfaAuthenticationChallengeResponseService.validateResponse(principal.getName(), credential.getResponse())) {
                //mfa验证没有通过
                throw new BadResponseException(principal.getName());
            }
        } catch (ChallengeException exception) {
            throw new NestedAuthenticationException(exception);
        } finally {
            //无论成功还是失败都关闭挑战
            mfaAuthenticationChallengeResponseService.closeChallenge(principal.getName());
        }
    }
}
