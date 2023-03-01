package com.jdcloud.gardener.camellia.uac.client.exception.client;

import com.jdcloud.gardener.camellia.uac.client.exception.ClientExceptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author zhanghan30
 * @date 2022/11/7 13:48
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ClientAlreadyExistedException extends ClientExceptions.ClientSideException {
    public ClientAlreadyExistedException(String clientId) {
        super(clientId);
    }
}
