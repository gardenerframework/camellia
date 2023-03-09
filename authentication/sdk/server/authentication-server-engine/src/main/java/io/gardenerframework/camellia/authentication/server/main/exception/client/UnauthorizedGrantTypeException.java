package io.gardenerframework.camellia.authentication.server.main.exception.client;

import io.gardenerframework.camellia.authentication.server.main.exception.AuthenticationServerAuthenticationExceptions;
import io.gardenerframework.camellia.authentication.server.main.exception.OAuth2ErrorCodes;
import io.gardenerframework.camellia.authentication.server.main.exception.annotation.OAuth2ErrorCode;
import io.gardenerframework.fragrans.messages.MessageArgumentsSupplier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author ZhangHan
 * @date 2022/5/13 2:36
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
@OAuth2ErrorCode(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT)
public class UnauthorizedGrantTypeException extends AuthenticationServerAuthenticationExceptions.ClientSideException implements MessageArgumentsSupplier {
    private final String grantType;

    public UnauthorizedGrantTypeException(String grantType) {
        super(grantType);
        this.grantType = grantType;
    }

    @Override
    public Object[] getMessageArguments() {
        return new Object[]{grantType};
    }
}
