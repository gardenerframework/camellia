package io.gardenerframework.camellia.authentication.server.main;

import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.exception.ChallengeResponseServiceException;
import io.gardenerframework.camellia.authentication.server.configuration.SmsAuthenticationServiceComponent;
import io.gardenerframework.camellia.authentication.server.main.annotation.AuthenticationType;
import io.gardenerframework.camellia.authentication.server.main.exception.client.BadSmsVerificationCodeException;
import io.gardenerframework.camellia.authentication.server.main.schema.UserAuthenticationRequestToken;
import io.gardenerframework.camellia.authentication.server.main.schema.credentials.SmsVerificationCodeCredentials;
import io.gardenerframework.camellia.authentication.server.main.schema.request.SmsAuthenticationParameter;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.MobilePhoneNumberPrincipal;
import io.gardenerframework.camellia.authentication.server.main.sms.challenge.SmsAuthenticationChallengeResponseService;
import io.gardenerframework.camellia.authentication.server.main.sms.challenge.SmsAuthenticationScenario;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Validator;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2021/12/27 12:44 下午
 */

@AuthenticationType("sms")
@AllArgsConstructor
@SmsAuthenticationServiceComponent
public class SmsAuthenticationService implements UserAuthenticationService {
    private final Validator validator;
    private final SmsAuthenticationChallengeResponseService challengeResponseService;

    @Override
    public UserAuthenticationRequestToken convert(@NonNull HttpServletRequest request, @Nullable OAuth2RequestingClient client, @NonNull Map<String, Object> context) throws AuthenticationException {
        SmsAuthenticationParameter smsAuthenticationParameter = new SmsAuthenticationParameter(request);
        //执行验证
        smsAuthenticationParameter.validate(validator);
        return new UserAuthenticationRequestToken(
                MobilePhoneNumberPrincipal.builder().name(smsAuthenticationParameter.getMobilePhoneNumber()).build(),
                SmsVerificationCodeCredentials.builder().code(smsAuthenticationParameter.getCode()).build()
        );
    }

    @Override
    public void authenticate(@NonNull UserAuthenticationRequestToken authenticationRequest, @Nullable OAuth2RequestingClient client, @NonNull User user, @NonNull Map<String, Object> context) throws AuthenticationException {
        try {
            SmsVerificationCodeCredentials credentials = (SmsVerificationCodeCredentials) authenticationRequest.getCredentials();
            //验证验证码
            if (!challengeResponseService.verifyResponse(
                    client, SmsAuthenticationScenario.class,
                    authenticationRequest.getPrincipal().getName(),
                    credentials.getCode()
            )) {
                //验证码不正确
                throw new BadSmsVerificationCodeException(credentials.getCode());
            }
            //验证成功，删除验证码以免重复使用
            challengeResponseService.closeChallenge(client, SmsAuthenticationScenario.class, authenticationRequest.getPrincipal().getName());
        } catch (ChallengeResponseServiceException e) {
            throw new InternalAuthenticationServiceException(e.getMessage(), e);
        }
    }
}
