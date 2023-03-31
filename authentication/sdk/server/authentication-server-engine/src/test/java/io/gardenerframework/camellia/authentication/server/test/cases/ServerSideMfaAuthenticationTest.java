package io.gardenerframework.camellia.authentication.server.test.cases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jdcloud.gardener.camellia.authorization.test.cases.TokenAuthenticationEntryPointTest;
import io.gardenerframework.camellia.authentication.server.main.exception.OAuth2ErrorCodes;
import io.gardenerframework.camellia.authentication.server.main.mfa.exception.client.MfaAuthenticationRequiredException;
import io.gardenerframework.camellia.authentication.server.main.mfa.utils.EmbeddedAuthenticationServerMfaAuthenticatorRegistry;
import io.gardenerframework.camellia.authentication.server.main.mfa.utils.MfaAuthenticationClientAuthenticationServerMfaAuthenticatorRegistry;
import io.gardenerframework.camellia.authentication.server.test.AuthenticationServerEngineTestApplication;
import io.gardenerframework.camellia.authentication.server.test.mfa.ServerSideMfaAuthenticator;
import io.gardenerframework.camellia.authentication.server.test.utils.TokenAuthenticationClient;
import io.gardenerframework.fragrans.messages.EnhancedMessageSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2023/3/31 17:53
 */
@SpringBootTest(classes = AuthenticationServerEngineTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("服务端mfa认证")
public class ServerSideMfaAuthenticationTest {
    @LocalServerPort
    private int port;
    @Autowired
    private EmbeddedAuthenticationServerMfaAuthenticatorRegistry embeddedAuthenticationServerMfaAuthenticatorRegistry;

    @Autowired
    private MfaAuthenticationClientAuthenticationServerMfaAuthenticatorRegistry mfaAuthenticationClientAuthenticationServerMfaAuthenticatorRegistry;

    @Autowired
    private TokenAuthenticationClient tokenAuthenticationClient;

    @Autowired
    private EnhancedMessageSource messageSource;

    @Test
    @DisplayName("检查认证器的加载是否没有问题")
    public void testForAuthenticatorRegistry() {
        //内嵌的不表达
        Assertions.assertNull(
                embeddedAuthenticationServerMfaAuthenticatorRegistry
                        .getAuthenticator("server-side")
        );
        //mfa client注册表中有
        Assertions.assertNotNull(
                mfaAuthenticationClientAuthenticationServerMfaAuthenticatorRegistry
                        .getAuthenticator("server-side")
        );
    }

    @DisplayName("冒烟测试")
    @Test
    public void smokeTest() throws JsonProcessingException {
        ServerSideMfaAuthenticator.setPort(port);
        tokenAuthenticationClient.setPort(port);
        HttpClientErrorException exception = null;
        try {
            tokenAuthenticationClient.login(
                    "server-side", new HashMap<>()
            );
        } catch (HttpClientErrorException e) {
            exception = e;
        }
        TokenAuthenticationEntryPointTest.OAuth2Error oAuth2Error = TokenAuthenticationEntryPointTest.OAuth2Error.create(exception);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        Assertions.assertEquals(
                messageSource.getMessage(MfaAuthenticationRequiredException.class, Locale.getDefault()),
                oAuth2Error.getErrorDescription()
        );
        Assertions.assertEquals(OAuth2ErrorCodes.MFA_AUTHENTICATION_REQUIRED, oAuth2Error.getError());
        Assertions.assertNotNull(oAuth2Error.getDetails().get("extField"));
        Assertions.assertEquals("server-side", oAuth2Error.getDetails().get("challengeAuthenticatorName"));
        Map<String, Object> request = new HashMap<>();
        request.put("challengeId", oAuth2Error.getDetails().get("id"));
        //执行校验
        request.put("response", "666");
        //执行mfa认证
        ResponseEntity<String> response = tokenAuthenticationClient.login(
                "mfa",
                request
        );
        Map<?, ?> userInfo = tokenAuthenticationClient.getUserInfo((String) new ObjectMapper().readValue(response.getBody(), Map.class).get("access_token"));
        Assertions.assertNotNull(userInfo);
    }
}
