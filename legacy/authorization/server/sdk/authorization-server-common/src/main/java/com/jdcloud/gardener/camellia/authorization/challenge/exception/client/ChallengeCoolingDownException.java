package com.jdcloud.gardener.camellia.authorization.challenge.exception.client;

import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.OAuth2ErrorCodes;
import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.annotation.OAuth2ErrorCode;
import com.jdcloud.gardener.fragrans.api.standard.error.exception.ApiErrorDetailsSupplier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZhangHan
 * @date 2022/5/17 0:15
 */
@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
@OAuth2ErrorCode(OAuth2ErrorCodes.INVALID_REQUEST)
public class ChallengeCoolingDownException extends ChallengeException implements ApiErrorDetailsSupplier {
    private final long ttl;

    public ChallengeCoolingDownException(String key, long ttl) {
        super(key);
        this.ttl = ttl;
    }

    @Override
    public Map<String, Object> getDetails() {
        Map<String, Object> details = new HashMap<>();
        details.put("cooldown", ttl);
        return details;
    }
}
