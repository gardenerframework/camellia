package com.jdcloud.gardener.camellia.authorization.authentication.main;

import com.jdcloud.gardener.camellia.authorization.authentication.main.client.ClientGroupProvider;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.SmsAuthenticationCodeChallengeRequest;
import com.jdcloud.gardener.camellia.authorization.challenge.WellKnownChallengeType;
import com.jdcloud.gardener.camellia.authorization.challenge.exception.client.InvalidChallengeException;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.Challenge;
import com.jdcloud.gardener.camellia.authorization.common.api.security.schema.AccessTokenDetails;
import com.jdcloud.gardener.camellia.sms.authentication.SmsAuthenticationCodeClient;
import com.jdcloud.gardener.camellia.sms.authentication.SmsAuthenticationCodeEncoder;
import com.jdcloud.gardener.camellia.sms.authentication.SmsAuthenticationCodeStore;
import com.jdcloud.gardener.camellia.sms.authentication.SmsAuthenticationPredefinedScenario;
import lombok.AllArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

/**
 * 给出一个短信验证码登录服务的基准类
 *
 * @author ZhangHan
 * @date 2022/5/16 8:13
 */
@AllArgsConstructor
public abstract class SmsAuthenticationCodeChallengeResponseServiceBase implements SmsAuthenticationCodeChallengeResponseService {
    private final SmsAuthenticationCodeEncoder smsAuthenticationCodeEncoder;
    private final SmsAuthenticationCodeStore smsAuthenticationCodeStore;
    private final SmsAuthenticationCodeClient smsAuthenticationCodeClient;
    private final ClientGroupProvider clientGroupProvider;
    /**
     * 这个只有在查询ttl接口时有用
     */
    private final AccessTokenDetails accessTokenDetails;

    /**
     * 生成验证码 - 子类有需要的话重写这个方法
     *
     * @param request 生成请求
     * @return 验证码
     */
    protected String generateCode(SmsAuthenticationCodeChallengeRequest request) {
        return String.format("%06d", new SecureRandom().nextInt(999999 + 1));
    }

    @Nullable
    @Override
    public Challenge sendChallenge(SmsAuthenticationCodeChallengeRequest request) {
        //经典6位验证码
        String code = generateCode(request);
        doSendChallenge(request, code);
        //保存验证码
        smsAuthenticationCodeStore.saveCode(
                request.getClientGroup(),
                request.getMobilePhoneNumber(),
                smsAuthenticationCodeEncoder.encode(
                        request.getClientGroup(),
                        code, SmsAuthenticationPredefinedScenario.LOGIN_AUTHENTICATION
                ),
                SmsAuthenticationPredefinedScenario.LOGIN_AUTHENTICATION,
                Duration.ofSeconds(getTtl())
        );
        return new Challenge(
                request.getMobilePhoneNumber(),
                WellKnownChallengeType.SMS,
                Date.from(Instant.now().plus(Duration.ofSeconds(getTtl()))),
                null
        );
    }

    /**
     * 把生成的验证码发出去
     *
     * @param code 验证码
     */
    protected void doSendChallenge(SmsAuthenticationCodeChallengeRequest request, String code) {
        smsAuthenticationCodeClient.sendCode(request.getHeaders(), request.getClientGroup(), request.getMobilePhoneNumber(), code, SmsAuthenticationPredefinedScenario.LOGIN_AUTHENTICATION);
    }

    /**
     * 验证码的有效期和重新发送时间
     * <p>
     * 留给子类用的，谁知道项目现场要多长时间有效
     *
     * @return 时间(秒)
     */
    protected abstract long getTtl();

    @Override
    public boolean validateResponse(String id, String response) throws InvalidChallengeException {
        String clientGroup = clientGroupProvider.getClientGroup(getRegisteredClientFromSecurityContext());
        String code = smsAuthenticationCodeStore.getCode(clientGroup, id, SmsAuthenticationPredefinedScenario.LOGIN_AUTHENTICATION);
        return Objects.equals(code, smsAuthenticationCodeEncoder.encode(clientGroup, response, SmsAuthenticationPredefinedScenario.LOGIN_AUTHENTICATION));
    }

    @Override
    public void closeChallenge(String id) {
        String clientGroup = clientGroupProvider.getClientGroup(getRegisteredClientFromSecurityContext());
        smsAuthenticationCodeStore.removeCode(clientGroup, id, SmsAuthenticationPredefinedScenario.LOGIN_AUTHENTICATION);
    }

    private RegisteredClient getRegisteredClientFromSecurityContext() {
        //从登录接口获取安全上下文
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2ClientAuthenticationToken) {
            return ((OAuth2ClientAuthenticationToken) authentication).getRegisteredClient();
        } else {
            return null;
        }
    }
}
