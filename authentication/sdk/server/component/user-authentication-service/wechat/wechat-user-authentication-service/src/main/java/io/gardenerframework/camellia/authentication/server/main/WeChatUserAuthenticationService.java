package io.gardenerframework.camellia.authentication.server.main;

import io.gardenerframework.camellia.authentication.server.configuration.WeChatUserAuthenticationServiceComponent;
import io.gardenerframework.camellia.authentication.server.configuration.WeChatUserAuthenticationServiceOption;
import io.gardenerframework.camellia.authentication.server.main.annotation.AuthenticationType;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.WeChatOpenIdPrincipal;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.client.RestTemplate;

import javax.validation.Validator;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2023/3/9 07:23
 */
@AuthenticationType("wechat")
@WeChatUserAuthenticationServiceComponent
public class WeChatUserAuthenticationService extends OAuth2BaseUserAuthenticationService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Setter(onMethod = @__(@Autowired), value = AccessLevel.PRIVATE)
    private WeChatUserAuthenticationServiceOption option;

    public WeChatUserAuthenticationService(@NonNull Validator validator, OAuth2StateStore oAuth2StateStore) {
        super(validator, oAuth2StateStore);
    }

    @Nullable
    @Override
    protected Principal getPrincipal(@NonNull String authorizationCode) throws Exception {
        Map<String, Object> response = restTemplate.getForObject(
                "https://api.weixin.qq.com/sns/oauth2/access_token?appid={appId}}" +
                        "&secret={appSecret}" +
                        "&code={code}" +
                        "&grant_type=authorization_code",
                Map.class,
                option.getAppId(),
                option.getAppSecret(),
                authorizationCode
        );
        if (response == null) {
            throw new InternalAuthenticationServiceException("no response");
        }
        if (response.get("errcode") != null) {
            throw new InternalAuthenticationServiceException("error = " + response.get("errmsg"));
        }
        return WeChatOpenIdPrincipal.builder().name(String.valueOf(response.get("openid"))).build();
    }
}
