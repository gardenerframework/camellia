package io.gardenerframework.camellia.authentication.server.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gardenerframework.camellia.authentication.server.configuration.JdUserAuthenticationServiceComponent;
import io.gardenerframework.camellia.authentication.server.configuration.JdUserAuthenticationServiceOption;
import io.gardenerframework.camellia.authentication.server.main.annotation.AuthenticationType;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.JdOpenIdPrincipal;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.JdXIdPrincipal;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
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
@AuthenticationType("jd")
@JdUserAuthenticationServiceComponent
public class JdUserAuthenticationService extends OAuth2BasedUserAuthenticationService {
    private final RestTemplate restTemplate = new RestTemplate();
    @Setter(onMethod = @__(@Autowired), value = AccessLevel.PRIVATE)
    private JdUserAuthenticationServiceOption option;

    public JdUserAuthenticationService(@NonNull Validator validator, OAuth2StateStore oAuth2StateStore) {
        super(validator, oAuth2StateStore);
    }

    @Nullable
    @Override
    protected Principal getPrincipal(@NonNull String authorizationCode) throws Exception {
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
        String userId = (String) response.get(option.isOpenIdAsUserId() ? "open_id" : "xid");
        if (userId == null) {
            //没有openId
            throw new InternalAuthenticationServiceException("error = " + new ObjectMapper().writeValueAsString(response));
        }
        return (option.isOpenIdAsUserId() ? JdOpenIdPrincipal.builder() : JdXIdPrincipal.builder())
                .name(userId)
                .build();

    }
}
