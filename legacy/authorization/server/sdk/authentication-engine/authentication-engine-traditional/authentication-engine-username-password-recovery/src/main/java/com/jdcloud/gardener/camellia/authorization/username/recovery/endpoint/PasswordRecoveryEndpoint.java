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
 * 处理密码召回功能
 *
 * @author zhanghan30
 * @date 2022/1/10 12:12 下午
 */
@AuthorizationServerRestController
@Slf4j
@RequestMapping("/me")
@LogTarget("密码找回接口")
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
     * 发送密码找回请求
     *
     * @param request 请求
     * @return 请求结果
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
                //服务报异常了，那就处理服务的异常
                throw exception;
            } else {
                //不然认为是账户出了什么异常，统一说是有问题就行了
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "", new BadCredentialsException(request.getUsername()));
            }
        }
        //擦除用户密码
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
     * 重置密码
     * <p>
     * 在重置密码的时候除去验证挑战id是否有效外，另外还要验证当前客户端是否可以重置密码，比如和回答了挑战的客户端是不是一个
     *
     * @param request 重置请求
     */
    @Override
    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        if (!passwordRecoveryService.validateResponse(request.getChallengeId(), request.getResponse())) {
            throw new BadResponseException(request.getChallengeId());
        }
        //后续由proxy完成user的注入
        passwordRecoveryService.resetPassword(request.getChallengeId(), request.getPassword());
        passwordRecoveryService.closeChallenge(request.getChallengeId());
    }
}
