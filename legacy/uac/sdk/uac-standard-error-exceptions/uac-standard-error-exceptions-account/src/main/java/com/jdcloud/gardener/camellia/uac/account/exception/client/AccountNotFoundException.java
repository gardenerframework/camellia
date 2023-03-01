package com.jdcloud.gardener.camellia.uac.account.exception.client;

import com.jdcloud.gardener.camellia.uac.account.exception.AccountExceptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author zhanghan30
 * @date 2022/9/20 17:49
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class AccountNotFoundException extends AccountExceptions.ClientSideException {
    public AccountNotFoundException(String accountId) {
        super(accountId);
    }
}
