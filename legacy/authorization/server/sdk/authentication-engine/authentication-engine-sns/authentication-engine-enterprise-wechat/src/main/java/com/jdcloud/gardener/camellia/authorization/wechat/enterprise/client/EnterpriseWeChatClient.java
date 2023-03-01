package com.jdcloud.gardener.camellia.authorization.wechat.enterprise.client;

import com.jdcloud.gardener.camellia.authorization.wechat.enterprise.client.schema.response.AccessTokenResponse;
import com.jdcloud.gardener.camellia.authorization.wechat.enterprise.client.schema.response.UserInfoResponse;
import com.jdcloud.gardener.camellia.authorization.wechat.enterprise.configuration.EnterpriseWeChatOption;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * @author ZhangHan
 * @date 2022/11/8 20:54
 */
@RequiredArgsConstructor
@Component
public class EnterpriseWeChatClient {
    private final RestTemplate restTemplate;
    private final EnterpriseWeChatOption option;
    private AccessToken token;

    private synchronized String obtainAccessToken() throws AuthenticationException {
        if (token == null || token.getTimestamp().plus(token.getTtl()).isBefore(Instant.now())) {
            //需要重新获取token
            AccessTokenResponse response = Objects.requireNonNull(restTemplate.getForObject("https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid={id}&corpsecret={secret}", AccessTokenResponse.class, option.getCorpId(), option.getAppSecret()));
            response.assertSuccess();
            token = new AccessToken(response.getAccess_token(), Instant.now(), Duration.ofSeconds(response.getExpires_in()));
        }
        return token.getToken();
    }

    public String getUserId(@NonNull String code) throws AuthenticationException {
        UserInfoResponse response = Objects.requireNonNull(restTemplate.getForObject("https://qyapi.weixin.qq.com/cgi-bin/auth/getuserinfo?access_token={token}&code={code}", UserInfoResponse.class, obtainAccessToken(), code));
        response.assertSuccess();
        return StringUtils.hasText(response.getUserId()) ? response.getUserId() : response.getOpenId();
    }

    @Getter
    @AllArgsConstructor
    private static class AccessToken {
        private String token;
        private Instant timestamp;
        private Duration ttl;
    }
}
