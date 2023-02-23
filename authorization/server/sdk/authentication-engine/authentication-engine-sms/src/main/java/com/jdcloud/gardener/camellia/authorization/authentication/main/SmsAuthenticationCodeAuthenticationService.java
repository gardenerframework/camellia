package com.jdcloud.gardener.camellia.authorization.authentication.main;

import com.jdcloud.gardener.camellia.authorization.authentication.main.annotation.AuthenticationType;
import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.NestedAuthenticationException;
import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.client.BadAuthenticationRequestParameterException;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.UserAuthenticationRequestToken;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.credentials.SmsAuthenticationCodeCredentials;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.MobilePhoneNumberPrincipal;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.request.SmsAuthenticationCodeParameter;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
import com.jdcloud.gardener.camellia.authorization.challenge.exception.client.BadResponseException;
import com.jdcloud.gardener.camellia.authorization.challenge.exception.client.ChallengeException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

/**
 * 短信验证服务
 *
 * @author ZhangHan
 * @date 2022/5/15 13:53
 */
@Component
@AuthenticationType("sms")
@AllArgsConstructor
public class SmsAuthenticationCodeAuthenticationService implements UserAuthenticationService {
    private final Validator validator;
    private final SmsAuthenticationCodeChallengeResponseService smsAuthenticationCodeChallengeResponseService;

    @Override
    public UserAuthenticationRequestToken convert(HttpServletRequest request) throws AuthenticationException {
        SmsAuthenticationCodeParameter parameter = new SmsAuthenticationCodeParameter(request);
        Set<ConstraintViolation<Object>> violations = validator.validate(parameter);
        if (!CollectionUtils.isEmpty(violations)) {
            throw new BadAuthenticationRequestParameterException(violations);
        }
        return new UserAuthenticationRequestToken(
                new MobilePhoneNumberPrincipal(parameter.getMobilePhoneNumber()),
                new SmsAuthenticationCodeCredentials(parameter.getCode())
        );
    }

    @Override
    public void authenticate(UserAuthenticationRequestToken authenticationRequest, User user) throws AuthenticationException {
        MobilePhoneNumberPrincipal principal = (MobilePhoneNumberPrincipal) authenticationRequest.getPrincipal();
        SmsAuthenticationCodeCredentials credentials = (SmsAuthenticationCodeCredentials) authenticationRequest.getCredentials();
        try {
            if (!smsAuthenticationCodeChallengeResponseService.validateResponse(principal.getName(), credentials.getCode())) {
                throw new BadResponseException(principal.getName());
            }
        } catch (ChallengeException exception) {
            throw new NestedAuthenticationException(exception);
        }
        //关闭挑战
        smsAuthenticationCodeChallengeResponseService.closeChallenge(principal.getName());
    }
}
