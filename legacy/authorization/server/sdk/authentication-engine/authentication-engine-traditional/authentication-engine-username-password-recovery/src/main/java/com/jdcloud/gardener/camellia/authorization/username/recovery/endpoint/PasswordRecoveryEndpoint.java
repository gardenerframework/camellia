package com.jdcloud.gardener.camellia.authorization.username.recovery.endpoint;

import com.jdcloud.gardener.camellia.authorization.authentication.main.client.ClientGroupProvider;
import com.jdcloud.gardener.camellia.authorization.authentication.main.client.schema.Client;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.UserService;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
import com.jdcloud.gardener.camellia.authorization.challenge.exception.client.BadResponseException;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.Challenge;
import com.jdcloud.gardener.camellia.authorization.common.api.group.AuthorizationServerRestController;
import com.jdcloud.gardener.camellia.authorization.common.api.security.AccessTokenProtectedEndpoint;
import com.jdcloud.gardener.camellia.authorization.common.api.security.schema.AccessTokenDetails;
import com.jdcloud.gardener.camellia.authorization.common.utils.HttpRequestUtils;
import com.jdcloud.gardener.camellia.authorization.username.UsernameResolver;
import com.jdcloud.gardener.camellia.authorization.username.recovery.PasswordRecoveryService;
import com.jdcloud.gardener.camellia.authorization.username.recovery.schema.challenge.PasswordRecoveryChallengeRequest;
import com.jdcloud.gardener.camellia.authorization.username.recovery.schema.request.RecoverPasswordRequest;
import com.jdcloud.gardener.camellia.authorization.username.recovery.schema.request.ResetPasswordRequest;
import com.jdcloud.gardener.camellia.authorization.username.recovery.schema.response.PasswordRecoveryChallengeResponse;
import com.jdcloud.gardener.fragrans.log.annotation.LogTarget;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * ????????????????????????
 *
 * @author zhanghan30
 * @date 2022/1/10 12:12 ??????
 */
@AuthorizationServerRestController
@Slf4j
@RequestMapping("/me")
@LogTarget("??????????????????")
@AccessTokenProtectedEndpoint(optional = true)
@AllArgsConstructor
@Component
public class PasswordRecoveryEndpoint implements PasswordRecoveryEndpointSkeleton {
    private final UsernameResolver usernameResolver;
    private final UserService userService;
    private final PasswordRecoveryService passwordRecoveryService;
    private final ClientGroupProvider clientGroupProvider;
    private final AccessTokenDetails accessTokenDetails;

    /**
     * ????????????????????????
     *
     * @param request ??????
     * @return ????????????
     */
    @Override
    @PostMapping("/password:recover")
    public PasswordRecoveryChallengeResponse startRecovery(HttpServletRequest httpServletRequest, @Valid @RequestBody RecoverPasswordRequest request) {
        User user;
        try {
            if ((user = userService.load(usernameResolver.resolve(request.getUsername(), request.getPrincipalType()), null)) == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "", new BadCredentialsException(request.getUsername()));
            }
        } catch (AuthenticationException exception) {
            if (exception instanceof AuthenticationServiceException) {
                //????????????????????????????????????????????????
                throw exception;
            } else {
                //????????????????????????????????????????????????????????????????????????
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "", new BadCredentialsException(request.getUsername()));
            }
        }
        //??????????????????
        user.eraseCredentials();
        Client client = accessTokenDetails.getClient();
        Challenge passwordRecoveryChallenge = passwordRecoveryService.sendChallenge(new PasswordRecoveryChallengeRequest(
                HttpRequestUtils.getSafeHttpHeaders(httpServletRequest),
                clientGroupProvider.getClientGroup(accessTokenDetails.getRegisteredClient()),
                client,
                user,
                request.getAuthenticator()
        ));
        Assert.notNull(passwordRecoveryChallenge, "passwordRecoveryChallenge must not be null");
        return new PasswordRecoveryChallengeResponse(passwordRecoveryChallenge.getId(), passwordRecoveryChallenge.getAuthenticator(), passwordRecoveryService.getCooldown(), passwordRecoveryChallenge.getParameters());
    }

    /**
     * ????????????
     * <p>
     * ??????????????????????????????????????????id?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     *
     * @param request ????????????
     */
    @Override
    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        if (!passwordRecoveryService.validateResponse(request.getChallengeId(), request.getResponse())) {
            throw new BadResponseException(request.getChallengeId());
        }
        //?????????proxy??????user?????????
        passwordRecoveryService.resetPassword(request.getChallengeId(), request.getPassword());
        passwordRecoveryService.closeChallenge(request.getChallengeId());
    }
}
