package com.jdcloud.gardener.camellia.authorization.challenge.exception.client;

import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.OAuth2ErrorCodes;
import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.annotation.OAuth2ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author ZhangHan
 * @date 2022/5/31 17:06
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
@OAuth2ErrorCode(OAuth2ErrorCodes.UNAUTHORIZED)
public class BadResponseException extends ChallengeException {
    public BadResponseException(String challengeId) {
        super(challengeId);
    }
}
