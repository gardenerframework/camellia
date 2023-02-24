package io.gardenerframework.camellia.authentication.server.main.mfa.exception.client;

import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEnginePreserved;
import io.gardenerframework.camellia.authentication.server.main.exception.AuthenticationServerAuthenticationExceptions;
import io.gardenerframework.camellia.authentication.server.main.exception.OAuth2ErrorCodes;
import io.gardenerframework.camellia.authentication.server.main.exception.annotation.OAuth2ErrorCode;
import io.gardenerframework.fragrans.api.standard.error.exception.ApiErrorDetailsSupplier;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ZhangHan
 * @date 2022/4/21 23:29
 */
@OAuth2ErrorCode(OAuth2ErrorCodes.MFA_AUTHENTICATION_REQUIRED)
@ResponseStatus(HttpStatus.UNAUTHORIZED)
@AuthenticationServerEnginePreserved
public class MfaAuthenticationRequiredException extends AuthenticationServerAuthenticationExceptions.ClientSideException implements ApiErrorDetailsSupplier {
    /**
     * mfa认证上下文
     */
    @Getter
    private final Challenge mfaAuthenticationChallenge;

    public MfaAuthenticationRequiredException(Challenge MfaAuthenticationChallenge) {
        super(MfaAuthenticationChallenge.getId());
        this.mfaAuthenticationChallenge = MfaAuthenticationChallenge;
    }

    @Override
    public Map<String, Object> getDetails() {
        Map<String, Object> details = new HashMap<>(3);
        details.put("authenticator", mfaAuthenticationChallenge.getAuthenticator());
        details.put("challengeId", mfaAuthenticationChallenge.getId());
        details.put("expiresAt", new SimpleDateFormat(StdDateFormat.DATE_FORMAT_STR_ISO8601).format(mfaAuthenticationChallenge.getExpiresAt()));
        details.put("parameters", mfaAuthenticationChallenge.getParameters());
        return details;
    }
}
