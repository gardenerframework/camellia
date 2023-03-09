package com.jdcloud.gardener.camellia.authorization.authentication.main.endpoint;

import com.jdcloud.gardener.camellia.authorization.authentication.main.SmsAuthenticationCodeChallengeResponseService;
import com.jdcloud.gardener.camellia.authorization.authentication.main.client.ClientGroupProvider;
import com.jdcloud.gardener.camellia.authorization.authentication.main.client.schema.Client;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.SmsAuthenticationCodeChallengeRequest;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.MobilePhoneNumberPrincipal;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.request.SendSmsAuthenticationCodeRequest;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.response.SendSmsAuthenticationCodeResponse;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.UserService;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
import com.jdcloud.gardener.camellia.authorization.challenge.ChallengeContextAccessor;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.Challenge;
import com.jdcloud.gardener.camellia.authorization.common.api.group.AuthorizationServerRestController;
import com.jdcloud.gardener.camellia.authorization.common.api.security.AccessTokenProtectedEndpoint;
import com.jdcloud.gardener.camellia.authorization.common.api.security.schema.AccessTokenDetails;
import com.jdcloud.gardener.camellia.authorization.common.utils.HttpRequestUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author zhanghan30
 * @date 2022/4/25 5:29 下午
 */
@AuthorizationServerRestController
@AllArgsConstructor
@Slf4j
@Component
@AccessTokenProtectedEndpoint(optional = true)
@RequestMapping("/authentication/sms")
public class SmsAuthenticationCodeEndpoint implements SmsAuthenticationCodeEndpointSkeleton {
    private final ClientGroupProvider clientGroupProvider;
    private final UserService userService;
    private final SmsAuthenticationCodeChallengeResponseService smsAuthenticationCodeChallengeResponseService;
    private final ChallengeContextAccessor challengeContextAccessor;
    //放到方法里不会引用到bean
    private final AccessTokenDetails accessTokenDetails;

    /**
     * 发送短信
     *
     * @param httpServletRequest http请求
     * @param request            发送请求
     * @return 发送结果
     */
    @Override
    @PostMapping
    public SendSmsAuthenticationCodeResponse sendCode(
            HttpServletRequest httpServletRequest,
            @Valid @RequestBody SendSmsAuthenticationCodeRequest request
    ) {
        Client client = accessTokenDetails.getClient();
        String clientGroup = clientGroupProvider.getClientGroup(accessTokenDetails.getRegisteredClient());
        User user = null;
        try {
            user = userService.load(new MobilePhoneNumberPrincipal(request.getMobilePhoneNumber()), null);
        } catch (BadCredentialsException | AccountStatusException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "", exception);
        }
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "", new BadCredentialsException(request.getMobilePhoneNumber()));
        }
        //发送验证码
        Challenge challenge = smsAuthenticationCodeChallengeResponseService.sendChallenge(new SmsAuthenticationCodeChallengeRequest(
                HttpRequestUtils.getSafeHttpHeaders(httpServletRequest),
                //fixed
                //之前是这个clientGroupProvider.getClientGroup(clientGroup),
                clientGroup,
                client,
                user,
                request.getMobilePhoneNumber()
        ));
        Assert.notNull(challenge, "challenge must not be null");
        //fixed 取服务的cd的时间
        return new SendSmsAuthenticationCodeResponse(smsAuthenticationCodeChallengeResponseService.getCooldown());
    }
}
