package com.jdcloud.gardener.camellia.uac.client.exception.client;

import com.jdcloud.gardener.camellia.uac.client.exception.ClientExceptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author zhanghan30
 * @date 2022/9/20 17:49
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ClientNotFoundException extends ClientExceptions.ClientSideException {
    public ClientNotFoundException(String clientId) {
        super(clientId);
    }
}
