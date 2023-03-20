package io.gardenerframework.camellia.authentication.server.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gardenerframework.camellia.authentication.server.configuration.WeChatUserAuthenticationServiceComponent;
import io.gardenerframework.camellia.authentication.server.configuration.WeChatUserAuthenticationServiceOption;
import io.gardenerframework.camellia.authentication.server.main.annotation.AuthenticationType;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.WeChatOpenIdPrincipal;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.WeChatUnionIdPrincipal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.client.RestTemplate;

import javax.validation.Validator;
import java.util.Collections;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2023/3/9 07:23
 */
@AuthenticationType("wechat")
@WeChatUserAuthenticationServiceComponent
public class WeChatUserAuthenticationService extends OAuth2BasedUserAuthenticationService
        implements InitializingBean {
    private final RestTemplate restTemplate = new RestTemplate();

    @Setter(onMethod = @__(@Autowired), value = AccessLevel.PRIVATE)
    private WeChatUserAuthenticationServiceOption option;

    public WeChatUserAuthenticationService(@NonNull Validator validator, OAuth2StateStore oAuth2StateStore) {
        super(validator, oAuth2StateStore);
    }

    @Override
    protected AccessToken obtainAccessToken(@NonNull String authorizationCode, @NonNull Map<String, Object> context) throws Exception {
        Map<String, Object> response = restTemplate.getForObject(
                "https://api.weixin.qq.com/sns/oauth2/access_token?appid={appId}" +
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
        String accessToken = (String) response.get("access_token");
        if (accessToken == null) {
            throw new InternalAuthenticationServiceException("error = " + new ObjectMapper().writeValueAsString(response));
        }
        return WeChatAccessToken.builder()
                .accessToken((String) response.get("access_token"))
                .refreshToken((String) response.get("refresh_token"))
                .expireIn(Long.valueOf((Integer)response.get("expires_in")))
                .scope((String) response.get("scope"))
                .openId((String) response.get("openid"))
                .unionId((String) response.get("unionid"))
                .build();


    }

    @Nullable
    @Override
    protected Principal getPrincipal(@NonNull AccessToken accessToken, @NonNull Map<String, Object> context) throws Exception {
        WeChatAccessToken weChatAccessToken = (WeChatAccessToken) accessToken;
        return (option.isOpenIdAsUserId() ?
                WeChatOpenIdPrincipal.builder().name(weChatAccessToken.getOpenId()) :
                WeChatUnionIdPrincipal.builder().name(weChatAccessToken.getUnionId()))
                .build();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //fix 微信坑逼 text/plain
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_PLAIN));
        restTemplate.getMessageConverters().add(converter);
    }

    @SuperBuilder
    @Setter
    @Getter
    public static class WeChatAccessToken extends AccessToken {
        @NonNull
        private String openId;
        @Nullable
        private String unionId;
    }
}
