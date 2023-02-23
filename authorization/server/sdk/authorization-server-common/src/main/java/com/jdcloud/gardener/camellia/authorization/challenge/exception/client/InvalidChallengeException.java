package com.jdcloud.gardener.camellia.authorization.challenge.exception.client;

import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.OAuth2ErrorCodes;
import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.annotation.OAuth2ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author ZhangHan
 * @date 2022/5/15 19:52
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
@OAuth2ErrorCode(OAuth2ErrorCodes.INVALID_REQUEST)
public class InvalidChallengeException extends ChallengeException {
    public InvalidChallengeException(String challengeId) {
        super(challengeId);
    }
}
