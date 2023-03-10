package com.jdcloud.gardener.camellia.authorization.test.cases;

import com.jdcloud.gardener.camellia.authorization.authentication.main.annotation.AuthenticationType;
import com.jdcloud.gardener.camellia.authorization.authentication.mfa.MfaAuthenticationService;
import com.jdcloud.gardener.camellia.authorization.challenge.WellKnownChallengeType;
import com.jdcloud.gardener.camellia.authorization.challenge.exception.client.InvalidChallengeException;
import com.jdcloud.gardener.camellia.authorization.test.AuthorizationServerEngineTestApplication;
import com.jdcloud.gardener.camellia.authorization.test.authentication.main.AccountStatusErrorRequest;
import com.jdcloud.gardener.camellia.authorization.test.authentication.main.MfaTriggerRequest;
import com.jdcloud.gardener.camellia.authorization.test.authentication.main.NullAuthenticationRequest;
import com.jdcloud.gardener.camellia.authorization.test.authentication.main.NullPrincipalRequest;
import com.jdcloud.gardener.camellia.authorization.test.utils.WebAuthenticationClient;
import com.jdcloud.gardener.fragrans.api.standard.error.DefaultApiErrorConstants;
import com.jdcloud.gardener.fragrans.messages.EnhancedMessageSource;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ZhangHan
 * @date 2022/4/23 2:03
 */
@SpringBootTest(classes = AuthorizationServerEngineTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DisplayName("????????????????????????")
public class WebAuthenticationEntryPointTest {
    @Autowired
    private WebAuthenticationClient authenticationClient;
    @Autowired
    private EnhancedMessageSource messageSource;

    @Test
    @DisplayName("????????????????????????????????????null")
    public void testNullUserAuthenticationRequestToken() throws UnsupportedEncodingException {
        WebAuthenticationError error = new WebAuthenticationError(authenticationClient.login(AnnotationUtils.findAnnotation(NullAuthenticationRequest.class, AuthenticationType.class).value(), null));
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), error.getStatus());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), error.getPhrase());
        Assertions.assertEquals(messageSource.getMessage(DefaultApiErrorConstants.GENERIC_ERROR, Locale.getDefault()), error.getMessage());
    }

    @Test
    @DisplayName("?????????????????????????????????null")
    public void testNullPrincipal() throws UnsupportedEncodingException {
        WebAuthenticationError error = new WebAuthenticationError(authenticationClient.login(AnnotationUtils.findAnnotation(NullPrincipalRequest.class, AuthenticationType.class).value(), null));
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), error.getStatus());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), error.getPhrase());
        Assertions.assertEquals(messageSource.getMessage(DefaultApiErrorConstants.GENERIC_ERROR, Locale.getDefault()), error.getMessage());
    }

    @Test
    @DisplayName("??????????????????")
    public void tesAccountStatusError() throws UnsupportedEncodingException {
        Map<String, Object> username = new HashMap<>();
        username.put("username", "locked");
        WebAuthenticationError error = new WebAuthenticationError(authenticationClient.login(AnnotationUtils.findAnnotation(AccountStatusErrorRequest.class, AuthenticationType.class).value(), username));
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), error.getStatus());
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.getReasonPhrase(), error.getPhrase());
        Assertions.assertEquals(messageSource.getMessage(new LockedException(""), Locale.getDefault()), error.getMessage());


        username.put("username", "disabled");
        error = new WebAuthenticationError(authenticationClient.login(AnnotationUtils.findAnnotation(AccountStatusErrorRequest.class, AuthenticationType.class).value(), username));
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), error.getStatus());
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.getReasonPhrase(), error.getPhrase());
        Assertions.assertEquals(messageSource.getMessage(new DisabledException(""), Locale.getDefault()), error.getMessage());

        username.put("username", "expired");
        error = new WebAuthenticationError(authenticationClient.login(AnnotationUtils.findAnnotation(AccountStatusErrorRequest.class, AuthenticationType.class).value(), username));
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), error.getStatus());
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.getReasonPhrase(), error.getPhrase());
        Assertions.assertEquals(messageSource.getMessage(new AccountExpiredException(""), Locale.getDefault()), error.getMessage());
    }

    private void assertLoginSuccess(ResponseEntity<Void> response) {
        Assertions.assertEquals("http://localhost:19090/welcome", response.getHeaders().get("Location").get(0));
    }

    @Test
    @DisplayName("?????????????????????")
    public void testErrorAuthorizationCode() {
        Map<String, Object> request = new HashMap<>();
        String username = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        request.put("username", username);
        request.put("password", password);
    }

    @Test
    @DisplayName("??????mfa??????")
    public void testTriggerMfa() throws UnsupportedEncodingException {
        Map<String, Object> request = new HashMap<>();
        String username = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        request.put("username", username);
        request.put("password", null);

        //??????????????????????????????
        WebAuthenticationError error = new WebAuthenticationError(authenticationClient.login(AnnotationUtils.findAnnotation(MfaTriggerRequest.class, AuthenticationType.class).value(), request));
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), error.getStatus());
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.getReasonPhrase(), error.getPhrase());
        Assertions.assertEquals(messageSource.getMessage(new BadCredentialsException(""), Locale.getDefault()), error.getMessage());

        //??????????????????mfa
        request.put("password", password);
        MfaAuthenticationRequest mfaAuthenticationRequest = new MfaAuthenticationRequest(authenticationClient.login(AnnotationUtils.findAnnotation(MfaTriggerRequest.class, AuthenticationType.class).value(), request));
        Assertions.assertEquals(WellKnownChallengeType.GOOGLE_AUTHENTICATOR, mfaAuthenticationRequest.getAuthenticator());
        Assertions.assertNotNull(mfaAuthenticationRequest.getChallengeId());
        String challengeId = mfaAuthenticationRequest.getChallengeId();

        //?????????mfa??????????????????mfa
        mfaAuthenticationRequest = new MfaAuthenticationRequest(authenticationClient.login(AnnotationUtils.findAnnotation(MfaTriggerRequest.class, AuthenticationType.class).value(), request));
        Assertions.assertEquals(WellKnownChallengeType.GOOGLE_AUTHENTICATOR, mfaAuthenticationRequest.getAuthenticator());
        Assertions.assertEquals(challengeId, mfaAuthenticationRequest.getChallengeId());

        //??????????????????challengeId
        request.put("challengeId", UUID.randomUUID().toString());
        request.put("response", UUID.randomUUID().toString());
        error = new WebAuthenticationError(authenticationClient.login(AnnotationUtils.findAnnotation(MfaAuthenticationService.class, AuthenticationType.class).value(), request));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), error.getStatus());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), error.getPhrase());
        Assertions.assertEquals(messageSource.getMessage(new InvalidChallengeException(""), Locale.getDefault()), error.getMessage());

        //????????????
        request.put("challengeId", challengeId);
        request.put("response", UUID.randomUUID().toString());
        assertLoginSuccess(authenticationClient.login(AnnotationUtils.findAnnotation(MfaAuthenticationService.class, AuthenticationType.class).value(), request));

        //??????????????????
        error = new WebAuthenticationError(authenticationClient.login(AnnotationUtils.findAnnotation(MfaAuthenticationService.class, AuthenticationType.class).value(), request));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), error.getStatus());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), error.getPhrase());
        Assertions.assertEquals(messageSource.getMessage(new InvalidChallengeException(""), Locale.getDefault()), error.getMessage());

        //??????????????????????????????
        request.put("username", username);
        request.put("password", password);
        assertLoginSuccess(authenticationClient.login(AnnotationUtils.findAnnotation(MfaTriggerRequest.class, AuthenticationType.class).value(), request));

    }


    @Data
    public static class WebAuthenticationError {
        private final Pattern pattern = Pattern.compile("^http://localhost:19090/error\\?status=(\\d+?)&phrase=(.+?)&message=(.+?)(&code=(.+?))?$");
        private final int status;
        private final String phrase;
        private final String message;

        public WebAuthenticationError(ResponseEntity<Void> response) throws UnsupportedEncodingException {
            Matcher matcher = pattern.matcher(response.getHeaders().get("Location").get(0));
            matcher.find();
            this.status = Integer.parseInt(matcher.group(1));
            this.phrase = matcher.group(2);
            this.message = URLDecoder.decode(matcher.group(3), "utf-8");
        }
    }

    @Data
    public static class MfaAuthenticationRequest {
        private final Pattern pattern = Pattern.compile("^http://localhost:19090/mfa\\?authenticator=(.+?)&challengeId=(.+?)&(.+?)$");
        private final String authenticator;
        private final String challengeId;

        public MfaAuthenticationRequest(ResponseEntity<Void> response) {
            Matcher matcher = pattern.matcher(response.getHeaders().get("Location").get(0));
            matcher.find();
            this.authenticator = matcher.group(1);
            this.challengeId = matcher.group(2);
        }
    }
}
