package io.gardenerframework.camellia.authentication.server.main;

import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import io.gardenerframework.camellia.authentication.server.configuration.AlipayUserAuthenticationServiceComponent;
import io.gardenerframework.camellia.authentication.server.configuration.AlipayUserAuthenticationServiceOption;
import io.gardenerframework.camellia.authentication.server.main.annotation.AuthenticationType;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.AlipayUserIdPrincipal;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.InternalAuthenticationServiceException;

import javax.validation.Validator;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2023/3/9 07:23
 */
@AuthenticationType("alipay")
@AlipayUserAuthenticationServiceComponent
public class AlipayUserAuthenticationService extends OAuth2BasedUserAuthenticationService implements InitializingBean {
    @Setter(onMethod = @__(@Autowired), value = AccessLevel.PRIVATE)
    private AlipayUserAuthenticationServiceOption option;

    private AlipayClient alipayClient;

    public AlipayUserAuthenticationService(@NonNull Validator validator, OAuth2StateStore oAuth2StateStore) {
        super(validator, oAuth2StateStore);
    }

    @Override
    protected AccessToken obtainAccessToken(@NonNull String authorizationCode, @NonNull Map<String, Object> context) throws Exception {
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setGrantType("authorization_code");
        request.setCode(authorizationCode);
        AlipaySystemOauthTokenResponse response = alipayClient.execute(request);
        if (!response.isSuccess()) {
            throw new InternalAuthenticationServiceException(response.getBody());
        }
        return AlipayAccessToken.builder()
                .accessToken(response.getAccessToken())
                .refreshToken(response.getRefreshToken())
                .expireIn(Long.parseLong(response.getExpiresIn()))
                .userId(response.getUserId())
                .build();
    }

    @Nullable
    @Override
    protected Principal getPrincipal(@NonNull AccessToken accessToken, @NonNull Map<String, Object> context) throws Exception {
        return AlipayUserIdPrincipal.builder()
                .name(((AlipayAccessToken) accessToken).getUserId())
                .build();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl("https://openapi.alipay.com/gateway.do");
        alipayConfig.setAppId(option.getAppId());
        alipayConfig.setPrivateKey(option.getPrivateKey());
        alipayConfig.setFormat("json");
        alipayConfig.setCharset("UTF-8");
        alipayConfig.setSignType("RSA2");
        alipayConfig.setAlipayPublicKey(option.getAliPublicKey());
        alipayClient = new DefaultAlipayClient(alipayConfig);
    }

    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    public static class AlipayAccessToken extends AccessToken {
        @NonNull
        private String userId;
    }
}
