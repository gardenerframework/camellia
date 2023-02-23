package com.jdcloud.gardener.camellia.authorization.authentication.main.exception.client;

import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.OAuth2AuthenticationEngineExceptions;
import com.jdcloud.gardener.fragrans.messages.MessageArgumentsSupplier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author zhanghan30
 * @date 2022/11/9 15:47
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidStateException extends OAuth2AuthenticationEngineExceptions.ClientSideException implements MessageArgumentsSupplier {
    private final String state;

    public InvalidStateException(String state) {
        super(state);
        this.state = state;
    }

    @Override
    public Object[] getMessageArguments() {
        return new Object[]{
                state
        };
    }
}
