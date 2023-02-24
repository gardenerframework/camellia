package io.gardenerframework.camellia.authentication.server.main.mfa.exception.client;

import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.AuthorizationServerAuthenticationExceptions;
import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.OAuth2ErrorCodes;
import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.annotation.OAuth2ErrorCode;
import com.jdcloud.gardener.camellia.authorization.common.annotation.AuthorizationEnginePreserved;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author ZhangHan
 * @date 2022/5/13 2:06
 */
@OAuth2ErrorCode(OAuth2ErrorCodes.INVALID_CLIENT)
@ResponseStatus(HttpStatus.UNAUTHORIZED)
@AuthorizationEnginePreserved
public class InvalidClientException extends AuthorizationServerAuthenticationExceptions.ClientSideException {

    public InvalidClientException(String challengeId, String clientGroup, @Nullable String clientId) {
        super(String.format("invalid client response mfa challenge: challenge id %s, client group %s, client id %s", challengeId, clientId, clientGroup));
    }
}
