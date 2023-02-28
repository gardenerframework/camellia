package io.gardenerframework.camellia.authentication.server.main.mfa;

import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.exception.ChallengeResponseServiceException;
import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEngineComponent;
import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEnginePreserved;
import io.gardenerframework.camellia.authentication.server.main.UserAuthenticationService;
import io.gardenerframework.camellia.authentication.server.main.annotation.AuthenticationType;
import io.gardenerframework.camellia.authentication.server.main.exception.NestedAuthenticationException;
import io.gardenerframework.camellia.authentication.server.main.mfa.exception.client.BadMfaAuthenticationResponseException;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.MfaAuthenticationChallengeResponseService;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.MfaAuthenticationScenario;
import io.gardenerframework.camellia.authentication.server.main.mfa.schema.credentials.MfaResponseCredentials;
import io.gardenerframework.camellia.authentication.server.main.mfa.schema.principal.MfaAuthenticationPrincipal;
import io.gardenerframework.camellia.authentication.server.main.mfa.schema.request.MfaResponseParameter;
import io.gardenerframework.camellia.authentication.server.main.mfa.utils.MfaAuthenticationChallengeResponseServiceRegistry;
import io.gardenerframework.camellia.authentication.server.main.schema.UserAuthenticationRequestToken;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import io.gardenerframework.camellia.authentication.server.main.utils.RequestingClientHolder;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Validator;
import java.util.Objects;

/**
 * @author zhanghan30
 * @date 2022/5/12 9:09 下午
 */
@AuthenticationServerEnginePreserved
@AuthenticationType(value = "mfa")
@AllArgsConstructor
@AuthenticationServerEngineComponent
public class MfaAuthenticationService implements UserAuthenticationService {
    private final Validator validator;
    private final MfaAuthenticationChallengeResponseServiceRegistry registry;

    @Override
    public UserAuthenticationRequestToken convert(@NonNull HttpServletRequest request) throws AuthenticationException {
        MfaResponseParameter mfaResponseParameter = new MfaResponseParameter(request);
        mfaResponseParameter.validate(validator);
        return new UserAuthenticationRequestToken(
                MfaAuthenticationPrincipal.builder()
                        .name(mfaResponseParameter.getChallengeId())
                        .authenticatorName(mfaResponseParameter.getAuthenticator())
                        .build(),
                MfaResponseCredentials.builder().response(mfaResponseParameter.getResponse()).build()
        );
    }

    @Override
    public void authenticate(@NonNull UserAuthenticationRequestToken authenticationRequest, @NonNull User user) throws AuthenticationException {
        MfaAuthenticationPrincipal principal = (MfaAuthenticationPrincipal) authenticationRequest.getPrincipal();
        MfaResponseCredentials credential = (MfaResponseCredentials) authenticationRequest.getCredentials();
        String authenticatorType = principal.getAuthenticatorName();
        MfaAuthenticationChallengeResponseServiceRegistry.MfaAuthenticationChallengeResponseServiceRegistryItem item = registry.getItem(authenticatorType);
        MfaAuthenticationChallengeResponseService service = Objects.requireNonNull(item).getService();
        RequestingClient requestingClient = RequestingClientHolder.getClient();
        try {
            if (!service.verifyResponse(
                    requestingClient,
                    MfaAuthenticationScenario.class,
                    principal.getName(), credential.getResponse())) {
                //mfa验证没有通过
                throw new BadMfaAuthenticationResponseException(principal.getName());
            }
        } catch (ChallengeResponseServiceException exception) {
            throw new NestedAuthenticationException(exception);
        } finally {
            //无论成功还是失败都关闭挑战
            try {
                service.closeChallenge(
                        requestingClient,
                        MfaAuthenticationScenario.class,
                        principal.getName()
                );
            } catch (ChallengeResponseServiceException e) {
                //omit
            }
        }
    }
}
