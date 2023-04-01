package io.gardenerframework.camellia.authentication.server.test.cases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import io.gardenerframework.camellia.authentication.server.main.annotation.AuthenticationType;
import io.gardenerframework.camellia.authentication.server.main.exception.OAuth2ErrorCodes;
import io.gardenerframework.camellia.authentication.server.main.mfa.MfaAuthenticationService;
import io.gardenerframework.camellia.authentication.server.main.mfa.exception.client.BadMfaRequestException;
import io.gardenerframework.camellia.authentication.server.main.mfa.exception.client.MfaRequiredException;
import io.gardenerframework.camellia.authentication.server.main.spring.oauth2.OAuth2AuthorizationIdModifier;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import io.gardenerframework.camellia.authentication.server.test.AuthenticationServerEngineTestApplication;
import io.gardenerframework.camellia.authentication.server.test.authentication.main.AccountStatusErrorRequest;
import io.gardenerframework.camellia.authentication.server.test.authentication.main.MfaTriggerRequest;
import io.gardenerframework.camellia.authentication.server.test.authentication.main.NullAuthenticationRequest;
import io.gardenerframework.camellia.authentication.server.test.utils.TokenAuthenticationClient;
import io.gardenerframework.fragrans.api.standard.error.DefaultApiErrorConstants;
import io.gardenerframework.fragrans.messages.EnhancedMessageSource;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * @author ZhangHan
 * @date 2022/5/13 13:48
 */
@SpringBootTest(classes = AuthenticationServerEngineTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("app登录接口测试")
@Import(TokenAuthenticationEntryPointTest.UserIdSingleAccessToken.class)
@Slf4j
public class TokenAuthenticationEntryPointTest {
    @Autowired
    private TokenAuthenticationClient authenticationClient;
    @Autowired
    private EnhancedMessageSource messageSource;
    @LocalServerPort
    private int port;

    @Test
    @DisplayName("测试转换的用户登录请求为null")
    public void testNullUserAuthenticationRequestToken() throws JsonProcessingException {
        authenticationClient.setPort(port);
        try {
            authenticationClient.login(AnnotationUtils.findAnnotation(NullAuthenticationRequest.class, AuthenticationType.class).value(), null);
        } catch (HttpStatusCodeException exception) {
            OAuth2Error oAuth2Error = OAuth2Error.create(exception);
            Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
            Assertions.assertEquals(
                    messageSource.getMessage(DefaultApiErrorConstants.GENERIC_ERROR, Locale.getDefault()),
                    oAuth2Error.getErrorDescription()
            );
            Assertions.assertEquals(OAuth2ErrorCodes.SERVER_ERROR, oAuth2Error.getError());
            return;
        }
        Assertions.fail();
    }


    @Test
    @DisplayName("账户错误测试")
    public void tesAccountStatusError() throws JsonProcessingException {
        authenticationClient.setPort(port);
        Map<String, Object> username = new HashMap<>();
        username.put("username", "locked");
        try {
            authenticationClient.login(AnnotationUtils.findAnnotation(AccountStatusErrorRequest.class, AuthenticationType.class).value(), username);
        } catch (HttpStatusCodeException exception) {
            OAuth2Error oAuth2Error = OAuth2Error.create(exception);
            Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
            Assertions.assertEquals(
                    messageSource.getMessage(new LockedException(""), Locale.getDefault()),
                    oAuth2Error.getErrorDescription()
            );
            Assertions.assertEquals(OAuth2ErrorCodes.UNAUTHORIZED, oAuth2Error.getError());
            return;
        }
        Assertions.fail();
    }

    @Test
    @DisplayName("触发mfa测试")
    public void testTriggerMfa() throws JsonProcessingException {
        authenticationClient.setPort(port);
        Map<String, Object> request = new HashMap<>();
        String username = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        request.put("username", username);
        request.put("password", null);

        //先弄出来一个错误密码
        Assertions.assertThrows(
                HttpClientErrorException.class,
                () -> {
                    authenticationClient.login(AnnotationUtils.findAnnotation(MfaTriggerRequest.class, AuthenticationType.class).value(), request);
                }
        );

        //正确登录要求mfa
        request.put("password", password);
        boolean mfaRequired = false;
        Map<String, Object> details = new HashMap<>();
        try {
            authenticationClient.login(AnnotationUtils.findAnnotation(MfaTriggerRequest.class, AuthenticationType.class).value(), request);
        } catch (HttpClientErrorException exception) {
            OAuth2Error oAuth2Error = OAuth2Error.create(exception);
            Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
            Assertions.assertEquals(
                    messageSource.getMessage(MfaRequiredException.class, Locale.getDefault()),
                    oAuth2Error.getErrorDescription()
            );
            Assertions.assertEquals(MfaRequiredException.class.getCanonicalName(), oAuth2Error.getErrorCode());
            Assertions.assertEquals(OAuth2ErrorCodes.MFA_REQUIRED, oAuth2Error.getError());
            details.putAll(oAuth2Error.details);
            mfaRequired = true;
        }
        Assertions.assertTrue(mfaRequired);
        Assertions.assertEquals("test", details.get("challengeAuthenticatorName"));
        Assertions.assertNotNull(details.get("id"));

        String challengeId = (String) details.get("id");

        //不进行mfa认证依然要求mfa
        mfaRequired = false;
        details.clear();
        try {
            authenticationClient.login(AnnotationUtils.findAnnotation(MfaTriggerRequest.class, AuthenticationType.class).value(), request);
        } catch (HttpClientErrorException exception) {
            OAuth2Error oAuth2Error = OAuth2Error.create(exception);
            Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
            Assertions.assertEquals(
                    messageSource.getMessage(MfaRequiredException.class, Locale.getDefault()),
                    oAuth2Error.getErrorDescription()
            );
            Assertions.assertEquals(OAuth2ErrorCodes.MFA_REQUIRED, oAuth2Error.getError());
            details.putAll(oAuth2Error.details);
            mfaRequired = true;
        }
        Assertions.assertTrue(mfaRequired);
        Assertions.assertEquals("test", details.get("challengeAuthenticatorName"));
        Assertions.assertEquals(challengeId, details.get("id"));

        //给一个错误的challengeId
        request.put("challengeId", UUID.randomUUID().toString());
        request.put("response", UUID.randomUUID().toString());
        boolean invalidMfaChallengeIdFound = false;
        try {
            authenticationClient.login(AnnotationUtils.findAnnotation(MfaAuthenticationService.class, AuthenticationType.class).value(), request);
        } catch (HttpClientErrorException exception) {
            OAuth2Error oAuth2Error = OAuth2Error.create(exception);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
            Assertions.assertEquals(
                    messageSource.getMessage(BadMfaRequestException.class, Locale.getDefault()),
                    oAuth2Error.getErrorDescription()
            );
            Assertions.assertEquals(OAuth2ErrorCodes.INVALID_REQUEST, oAuth2Error.getError());
            invalidMfaChallengeIdFound = true;
        }
        Assertions.assertTrue(invalidMfaChallengeIdFound);

        //执行认证
        request.put("challengeId", challengeId);
        request.put("response", UUID.randomUUID().toString());
        authenticationClient.login(AnnotationUtils.findAnnotation(MfaAuthenticationService.class, AuthenticationType.class).value(), request);

        //之后正常登录也没问题
        request.put("username", username);
        request.put("password", password);
        //获取token
        ResponseEntity<String> login = authenticationClient.login(AnnotationUtils.findAnnotation(MfaTriggerRequest.class, AuthenticationType.class).value(), request);
        Map<String, Object> token = new ObjectMapper().readValue(login.getBody(), new TypeReference<Map<String, Object>>() {
        });
        Assertions.assertNotNull(token.get("access_token"));
        //获取用户信息
        Map<?, ?> userInfo = authenticationClient.getUserInfo((String) token.get("access_token"));
        Assertions.assertNotNull(userInfo.get("sub"));
        authenticationClient.setToken(null);
    }

    @Test
    @DisplayName("测试不断重复拿token换用户信息")
    public void accessTokenRepeatTest() throws JsonProcessingException {
        authenticationClient.setPort(port);
        Map<String, Object> request = new HashMap<>();
        String username = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        request.put("username", username);
        request.put("password", password);
        ResponseEntity<String> login = authenticationClient.login(AnnotationUtils.findAnnotation(MfaTriggerRequest.class, AuthenticationType.class).value(), request);
        Map<String, Object> token = new ObjectMapper().readValue(login.getBody(), new TypeReference<Map<String, Object>>() {
        });
        Assertions.assertNotNull(token);
        String accessToken = (String) token.get("access_token");
        for (int i = 0; i < 100; i++) {
            Map<?, ?> userInfo = authenticationClient.getUserInfo(accessToken);
            log.info("用户信息: " + new ObjectMapper().writeValueAsString(userInfo));
        }
    }

    @Test
    @DisplayName("测试token有效期")
    public void accessTokenTtl() throws JsonProcessingException, InterruptedException {
        authenticationClient.setPort(port);
        Map<String, Object> request = new HashMap<>();
        String username = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        request.put("username", username);
        request.put("password", password);
        request.put("token_ttl", Duration.ofSeconds(3).getSeconds());
        ResponseEntity<String> login = authenticationClient.login(AnnotationUtils.findAnnotation(MfaTriggerRequest.class, AuthenticationType.class).value(), request);
        Map<String, Object> token = new ObjectMapper().readValue(login.getBody(), new TypeReference<Map<String, Object>>() {
        });
        Assertions.assertNotNull(token.get("access_token"));
        Assertions.assertTrue((int) token.get("expires_in") <= Duration.ofSeconds(3).getSeconds());
        Thread.sleep(3000);
        try {
            authenticationClient.getUserInfo((String) token.get("access_token"));
        } catch (HttpClientErrorException exception) {
            OAuth2Error oAuth2Error = new ObjectMapper().readValue(exception.getResponseBodyAsString(), OAuth2Error.class);
            Assertions.assertEquals(OAuth2ErrorCodes.INVALID_TOKEN, oAuth2Error.getError());
            return;
        } finally {
            authenticationClient.setToken(null);
        }
        Assertions.fail();
    }

    @Test
    @DisplayName("测试单一登录态")
    public void singleAccessToken() throws JsonProcessingException, InterruptedException {
        authenticationClient.setPort(port);
        Map<String, Object> request = new HashMap<>();
        String username = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        request.put("username", username);
        request.put("password", password);
        request.put("token_ttl", Duration.ofSeconds(30).getSeconds());
        request.put("singleAccessToken", true);
        ResponseEntity<String> login = authenticationClient.login(AnnotationUtils.findAnnotation(MfaTriggerRequest.class, AuthenticationType.class).value(), request);
        Map<String, Object> token = new ObjectMapper().readValue(login.getBody(), new TypeReference<Map<String, Object>>() {
        });
        Assertions.assertNotNull(token.get("access_token"));
        Assertions.assertTrue((int) token.get("expires_in") <= Duration.ofSeconds(30).getSeconds());
        String willFailed = (String) token.get("access_token");
        authenticationClient.getUserInfo(willFailed);
        authenticationClient.setToken(null);
        Thread.sleep(1000);
        login = authenticationClient.login(AnnotationUtils.findAnnotation(MfaTriggerRequest.class, AuthenticationType.class).value(), request);
        token = new ObjectMapper().readValue(login.getBody(), new TypeReference<Map<String, Object>>() {
        });
        String willSuccess = (String) token.get("access_token");
        Assertions.assertNotEquals(willFailed, willSuccess);
        boolean failed = false;
        try {
            authenticationClient.getUserInfo(willFailed);
        } catch (HttpClientErrorException exception) {
            OAuth2Error oAuth2Error = new ObjectMapper().readValue(exception.getResponseBodyAsString(), OAuth2Error.class);
            Assertions.assertEquals(OAuth2ErrorCodes.INVALID_TOKEN, oAuth2Error.getError());
            failed = true;
        }
        Assertions.assertTrue(failed);
        authenticationClient.getUserInfo(willSuccess);
        authenticationClient.setToken(null);
    }

    public static class UserIdSingleAccessToken implements OAuth2AuthorizationIdModifier {

        @Override
        public String modify(@NonNull String originalId, @NonNull HttpServletRequest request, @Nullable RegisteredClient client, @Nullable User user) {
            if (request.getParameter("singleAccessToken") != null) {
                return String.format("%s.%s", client.getClientId(), user.getId());
            } else {
                return originalId;
            }
        }
    }

    @Data
    @NoArgsConstructor
    public static class OAuth2Error {
        private String error;
        private String errorDescription;
        private Map<String, Object> details;
        private String errorCode;

        public static OAuth2Error create(HttpStatusCodeException exception) throws JsonProcessingException {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
            return mapper.readValue(exception.getResponseBodyAsString(), OAuth2Error.class);
        }
    }
}
