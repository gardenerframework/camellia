package io.gardenerframework.camellia.authentication.server.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gardenerframework.camellia.authentication.server.configuration.JdUserAuthenticationServiceComponent;
import io.gardenerframework.camellia.authentication.server.configuration.JdUserAuthenticationServiceOption;
import io.gardenerframework.camellia.authentication.server.main.annotation.AuthenticationType;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.JdOpenIdPrincipal;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.JdXIdPrincipal;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import lombok.*;
import lombok.experimental.SuperBuilder;
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
@AuthenticationType("jd")
@JdUserAuthenticationServiceComponent
public class JdUserAuthenticationService extends OAuth2BasedUserAuthenticationService {
    private final RestTemplate restTemplate = new RestTemplate();
    @Setter(onMethod = @__(@Autowired), value = AccessLevel.PRIVATE)
    private JdUserAuthenticationServiceOption option;

    public JdUserAuthenticationService(@NonNull Validator validator, OAuth2StateStore oAuth2StateStore) {
        super(validator, oAuth2StateStore);
    }

    @Override
    protected AccessToken obtainAccessToken(@NonNull String authorizationCode, @NonNull Map<String, Object> context) throws Exception {
        Map<String, Object> response = restTemplate.getForObject(
                "https://open-oauth.jd.com/oauth2/access_token?app_key={appId}&app_secret={appSecret}&grant_type=authorization_code&code={code}",
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
            //没有openId
            throw new InternalAuthenticationServiceException("error = " + new ObjectMapper().writeValueAsString(response));
        }
        return JdAccessToken.builder()
                .accessToken((String) response.get("access_token"))
                .refreshToken((String) response.get("refresh_token"))
                .expireIn(Long.valueOf((Integer) response.get("expires_in")))
                .scope((String) response.get("scope"))
                .openId((String) response.get("open_id"))
                .xid((String) response.get("xid"))
                .build();
    }

    @Nullable
    @Override
    protected Principal getPrincipal(@NonNull AccessToken accessToken, @NonNull Map<String, Object> context) throws Exception {
        JdAccessToken jdAccessToken = (JdAccessToken) accessToken;
        return (option.isOpenIdAsUserId() ? JdOpenIdPrincipal.builder() : JdXIdPrincipal.builder().name(jdAccessToken.getXid()))
                .build();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class JdAccessToken extends AccessToken {
        @NonNull
        private String openId;
        @NonNull
        private String xid;
    }
}
