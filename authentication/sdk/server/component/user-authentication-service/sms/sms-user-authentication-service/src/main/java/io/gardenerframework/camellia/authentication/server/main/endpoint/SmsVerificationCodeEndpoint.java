package io.gardenerframework.camellia.authentication.server.main.endpoint;

import io.gardenerframework.camellia.authentication.infra.challenge.core.exception.ChallengeInCooldownException;
import io.gardenerframework.camellia.authentication.infra.challenge.core.exception.ChallengeResponseServiceException;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.sms.challenge.schema.SmsVerificationCodeChallengeRequest;
import io.gardenerframework.camellia.authentication.server.common.api.group.AuthenticationServerRestController;
import io.gardenerframework.camellia.authentication.server.configuration.SmsAuthenticationServiceComponent;
import io.gardenerframework.camellia.authentication.server.main.exception.client.SmsVerificationCodeNotReadyException;
import io.gardenerframework.camellia.authentication.server.main.exception.client.UserNotFoundException;
import io.gardenerframework.camellia.authentication.server.main.schema.request.SendSmsVerificationCodeRequest;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.MobilePhoneNumberPrincipal;
import io.gardenerframework.camellia.authentication.server.main.sms.challenge.SmsAuthenticationChallengeResponseService;
import io.gardenerframework.camellia.authentication.server.main.sms.challenge.SmsAuthenticationScenario;
import io.gardenerframework.camellia.authentication.server.main.user.UserService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.HashMap;

@AuthenticationServerRestController
@RequestMapping("/authentication/sms/code")
@SmsAuthenticationServiceComponent
@AllArgsConstructor
public class SmsVerificationCodeEndpoint {
    /**
     * 用户服务
     */
    @NonNull
    private final UserService userService;
    /**
     *
     */
    @NonNull
    private final SmsAuthenticationChallengeResponseService service;

    @PostMapping
    public Challenge sendVerificationCode(
            @Valid @RequestBody SendSmsVerificationCodeRequest request
    ) throws ChallengeResponseServiceException {
        try {
            if (userService.load(
                    MobilePhoneNumberPrincipal.builder()
                            .name(request.getMobilePhoneNumber())
                            .build(),
                    new HashMap<>()
            ) == null) {
                throw new UserNotFoundException(request.getMobilePhoneNumber());
            }
            return service.sendChallenge(
                    null,
                    SmsAuthenticationScenario.class,
                    SmsVerificationCodeChallengeRequest.builder()
                            .mobilePhoneNumber(request.getMobilePhoneNumber())
                            .build()
            );
        } catch (ChallengeInCooldownException e) {
            //不能发送
            throw new SmsVerificationCodeNotReadyException(request.getMobilePhoneNumber(), e.getTimeRemaining());
        }
    }
}
