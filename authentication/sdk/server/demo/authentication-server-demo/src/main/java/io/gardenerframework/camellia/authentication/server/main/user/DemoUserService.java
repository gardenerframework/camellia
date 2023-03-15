package io.gardenerframework.camellia.authentication.server.main.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gardenerframework.camellia.authentication.server.main.AlipayUserAuthenticationService;
import io.gardenerframework.camellia.authentication.server.main.JdUserAuthenticationService;
import io.gardenerframework.camellia.authentication.server.main.OAuth2BasedUserAuthenticationService;
import io.gardenerframework.camellia.authentication.server.main.WeChatUserAuthenticationService;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.credentials.PasswordCredentials;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.WeChatOpenIdPrincipal;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.WeChatUnionIdPrincipal;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DemoUserService implements UserService {
    private final String password = "123456";

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
        OAuth2BasedUserAuthenticationService.AccessToken weChatAccessToken = weChatUserAuthenticationService.getAccessTokenFromContext(context);
        OAuth2BasedUserAuthenticationService.AccessToken alipayAccessToken = alipayUserAuthenticationService.getAccessTokenFromContext(context);
        OAuth2BasedUserAuthenticationService.AccessToken jdAccessToken = jdUserAuthenticationService.getAccessTokenFromContext(context);
        if (weChatAccessToken != null && (principal instanceof WeChatOpenIdPrincipal || principal instanceof WeChatUnionIdPrincipal)) {
            return obtainWeChatUser(principal, weChatAccessToken);
        }
        return User.builder()
                .id(principal.getName())
                .credential(PasswordCredentials.builder().password(password).build())
                .name(principal.getName())
                .avatar("https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg")
                .enabled(!principal.getName().equals("disabled"))
                .locked(principal.getName().equals("locked"))
                .principal(principal)
                .build();
    }

    private User obtainWeChatUser(@NonNull Principal principal, @NonNull OAuth2BasedUserAuthenticationService.AccessToken accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_PLAIN));
        restTemplate.getMessageConverters().add(converter);
        Map<String, Object> response = restTemplate.getForObject(
                "https://api.weixin.qq.com/sns/userinfo?access_token={accessToken}&openid={principal}",
                Map.class,
                accessToken.getAccessToken(), principal.getName()
        );
        if (response.get("nickname") == null) {
            try {
                throw new InternalAuthenticationServiceException(new ObjectMapper().writeValueAsString(response));
            } catch (JsonProcessingException e) {
                throw new InternalAuthenticationServiceException(e.getMessage(), e);
            }
        }
        return User.builder()
                .id(principal.getName())
                .credential(PasswordCredentials.builder().password(password).build())
                .name((String) response.get("nickname"))
                .avatar((String) response.get("headimgurl"))
                .enabled(true)
                .locked(false)
                .principal(principal)
                .build();
    }
}
