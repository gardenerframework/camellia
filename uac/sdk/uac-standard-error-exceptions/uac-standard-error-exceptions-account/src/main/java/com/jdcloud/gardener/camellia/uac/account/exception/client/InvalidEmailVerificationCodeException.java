package com.jdcloud.gardener.camellia.uac.account.exception.client;

import com.jdcloud.gardener.camellia.uac.account.exception.AccountExceptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author zhanghan30
 * @date 2022/11/15 19:39
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidEmailVerificationCodeException extends AccountExceptions.ClientSideException {
}
