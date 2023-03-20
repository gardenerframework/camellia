package io.gardenerframework.camellia.authentication.server.main.user;

import io.gardenerframework.camellia.authentication.server.configuration.AlipayUserAuthenticationServiceOption;
import io.gardenerframework.camellia.authentication.server.main.AlipayUserAuthenticationService;
import io.gardenerframework.camellia.authentication.server.main.JdUserAuthenticationService;
import io.gardenerframework.camellia.authentication.server.main.WeChatUserAuthenticationService;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.credentials.PasswordCredentials;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DemoUserService implements UserService {
    private final String password = "123456";
    private final AlipayUserAuthenticationServiceOption option;
    private final WeChatUserAuthenticationService weChatUserAuthenticationService;
    private final AlipayUserAuthenticationService alipayUserAuthenticationService;
    private final JdUserAuthenticationService jdUserAuthenticationService;

    @Nullable
    @Override
    public User authenticate(@NonNull Principal principal, @NonNull PasswordCredentials credentials, @Nullable Map<String, Object> context) throws AuthenticationException {
        if (Objects.equals(credentials.getPassword(), password)) {
            return load(principal, context);
        } else {
            throw new BadCredentialsException(principal.getName());
        }
    }

    @Nullable
    @Override
    public User load(@NonNull Principal principal, @Nullable Map<String, Object> context) throws AuthenticationException, UnsupportedOperationException {
        return "404".equals(principal.getName()) ? null : User.builder()
                .id(principal.getName())
                .credential(PasswordCredentials.builder().password(password).build())
                .name(principal.getName())
                .avatar("https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg")
                .enabled(!principal.getName().equals("disabled"))
                .locked(principal.getName().equals("locked"))
                .principal(principal)
                .build();
    }

//    private User obtainWeChatUser(@NonNull Principal principal, @NonNull OAuth2BasedUserAuthenticationService.AccessToken accessToken) {
//        RestTemplate restTemplate = new RestTemplate();
//        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
//        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_PLAIN));
//        restTemplate.getMessageConverters().add(converter);
//        Map<String, Object> response = restTemplate.getForObject(
//                "https://api.weixin.qq.com/sns/userinfo?access_token={accessToken}&openid={principal}",
//                Map.class,
//                accessToken.getAccessToken(), principal.getName()
//        );
//        if (response.get("nickname") == null) {
//            try {
//                throw new InternalAuthenticationServiceException(new ObjectMapper().writeValueAsString(response));
//            } catch (JsonProcessingException e) {
//                throw new InternalAuthenticationServiceException(e.getMessage(), e);
//            }
//        }
//        return User.builder()
//                .id(principal.getName())
//                .credential(PasswordCredentials.builder().password(password).build())
//                .name((String) response.get("nickname"))
//                .avatar((String) response.get("headimgurl"))
//                .enabled(true)
//                .locked(false)
//                .principal(principal)
//                .build();
//    }
//
//    private User obtainAlipayUser(@NonNull Principal principal, @NonNull OAuth2BasedUserAuthenticationService.AccessToken accessToken) {
//        AlipayUserAuthenticationService.AlipayAccessToken alipayAccessToken = (AlipayUserAuthenticationService.AlipayAccessToken) accessToken;
//        try {
//            AlipayConfig alipayConfig = new AlipayConfig();
//            alipayConfig.setServerUrl("https://openapi.alipay.com/gateway.do");
//            alipayConfig.setAppId(option.getAppId());
//            alipayConfig.setPrivateKey(option.getPrivateKey());
//            alipayConfig.setFormat("json");
//            alipayConfig.setCharset("UTF-8");
//            alipayConfig.setSignType("RSA2");
//            alipayConfig.setAlipayPublicKey(option.getAliPublicKey());
//            AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);
//            AlipayUserUserinfoShareRequest request = new AlipayUserUserinfoShareRequest();
//            AlipayUserUserinfoShareResponse response = alipayClient.execute(request, accessToken.getAccessToken());
//            if (!response.isSuccess()) {
//                if (!"40006".equals(response.getCode())) {
//                    throw new InternalAuthenticationServiceException(response.getBody());
//                } else {
//                    return User.builder()
//                            .id(principal.getName())
//                            .credential(PasswordCredentials.builder().password(password).build())
//                            .name(alipayAccessToken.getUserId())
//                            .avatar(null)
//                            .enabled(true)
//                            .locked(false)
//                            .principal(principal)
//                            .build();
//                }
//            }
//            return User.builder()
//                    .id(principal.getName())
//                    .credential(PasswordCredentials.builder().password(password).build())
//                    .name(response.getRealName())
//                    .avatar(response.getAvatar())
//                    .enabled(true)
//                    .locked(false)
//                    .principal(principal)
//                    .build();
//        } catch (AuthenticationException e) {
//            throw e;
//        } catch (Exception e) {
//            throw new InternalAuthenticationServiceException(e.getMessage(), e);
//        }
//    }
}
