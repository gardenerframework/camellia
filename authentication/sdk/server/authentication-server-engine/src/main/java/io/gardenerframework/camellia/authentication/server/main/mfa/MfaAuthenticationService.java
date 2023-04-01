package io.gardenerframework.camellia.authentication.server.main.mfa;

import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.exception.ChallengeResponseServiceException;
import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEngineComponent;
import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEnginePreserved;
import io.gardenerframework.camellia.authentication.server.main.UserAuthenticationService;
import io.gardenerframework.camellia.authentication.server.main.annotation.AuthenticationType;
import io.gardenerframework.camellia.authentication.server.main.exception.NestedAuthenticationException;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.AuthenticationServerMfaAuthenticationChallengeResponseService;
import io.gardenerframework.camellia.authentication.server.main.mfa.exception.client.BadMfaResponseException;
import io.gardenerframework.camellia.authentication.server.main.mfa.schema.credentials.MfaResponseCredentials;
import io.gardenerframework.camellia.authentication.server.main.mfa.schema.principal.MfaPrincipal;
import io.gardenerframework.camellia.authentication.server.main.mfa.schema.request.MfaResponseParameter;
import io.gardenerframework.camellia.authentication.server.main.schema.UserAuthenticationRequestToken;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Validator;
import java.util.Map;

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
    private final AuthenticationServerMfaAuthenticationChallengeResponseService authenticationServerMfaAuthenticationChallengeResponseService;

    @Override
    public UserAuthenticationRequestToken convert(
            @NonNull HttpServletRequest request,
            @Nullable OAuth2RequestingClient client,
            @NonNull Map<String, Object> context
    ) throws AuthenticationException {
        MfaResponseParameter mfaResponseParameter = new MfaResponseParameter(request);
        mfaResponseParameter.validate(validator);
        return new UserAuthenticationRequestToken(
                MfaPrincipal.builder()
                        .name(mfaResponseParameter.getChallengeId())
                        .build(),
                MfaResponseCredentials.builder().response(mfaResponseParameter.getResponse()).build()
        );
    }

    @Override
    public void authenticate(
            @NonNull UserAuthenticationRequestToken authenticationRequest,
            @Nullable OAuth2RequestingClient client,
            @NonNull User user,
            @NonNull Map<String, Object> context
    ) throws AuthenticationException {
        MfaPrincipal principal = (MfaPrincipal) authenticationRequest.getPrincipal();
        MfaResponseCredentials credential = (MfaResponseCredentials) authenticationRequest.getCredentials();
        try {
            //尝试验证
            if (!authenticationServerMfaAuthenticationChallengeResponseService.verifyResponse(
                    client,
                    authenticationServerMfaAuthenticationChallengeResponseService.getClass(),
                    principal.getName(), credential.getResponse())) {
                //mfa验证没有通过
                throw new BadMfaResponseException(principal.getName());
            } else {
                //fix 完成验证后关闭
                authenticationServerMfaAuthenticationChallengeResponseService.closeChallenge(
                        client,
                        authenticationServerMfaAuthenticationChallengeResponseService.getClass(),
                        principal.getName()
                );
            }
        } catch (ChallengeResponseServiceException exception) {
            throw new NestedAuthenticationException(exception);
        }
    }
}
