package io.gardenerframework.camellia.authentication.server.main.exception.client;

import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.AuthorizationServerAuthenticationExceptions;
import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.OAuth2ErrorCodes;
import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.annotation.OAuth2ErrorCode;
import com.jdcloud.gardener.fragrans.messages.MessageArgumentsSupplier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author ZhangHan
 * @date 2022/5/13 2:36
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
@OAuth2ErrorCode(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT)
public class UnauthorizedGrantTypeException extends AuthorizationServerAuthenticationExceptions.ClientSideException implements MessageArgumentsSupplier {
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
