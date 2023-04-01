package io.gardenerframework.camellia.authentication.server.main.mfa.exception.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEnginePreserved;
import io.gardenerframework.camellia.authentication.server.main.exception.AuthenticationServerAuthenticationExceptions;
import io.gardenerframework.camellia.authentication.server.main.exception.OAuth2ErrorCodes;
import io.gardenerframework.camellia.authentication.server.main.exception.annotation.OAuth2ErrorCode;
import io.gardenerframework.fragrans.api.standard.error.exception.ApiErrorDetailsSupplier;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

/**
 * @author ZhangHan
 * @date 2022/4/21 23:29
 */
@OAuth2ErrorCode(OAuth2ErrorCodes.MFA_REQUIRED)
@ResponseStatus(HttpStatus.UNAUTHORIZED)
@AuthenticationServerEnginePreserved
public class MfaRequiredException extends AuthenticationServerAuthenticationExceptions.ClientSideException implements ApiErrorDetailsSupplier {
    /**
     * mfa认证上下文
     */
    @Getter
    @NonNull
    private final Challenge mfaAuthenticationChallenge;

    public MfaRequiredException(@NonNull Challenge MfaAuthenticationChallenge) {
        super(MfaAuthenticationChallenge.getId());
        this.mfaAuthenticationChallenge = MfaAuthenticationChallenge;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getDetails() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new StdDateFormat());
        return objectMapper.convertValue(mfaAuthenticationChallenge, Map.class);
    }
}
