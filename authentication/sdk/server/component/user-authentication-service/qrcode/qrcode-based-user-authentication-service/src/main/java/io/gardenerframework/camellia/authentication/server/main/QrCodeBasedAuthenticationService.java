package io.gardenerframework.camellia.authentication.server.main;

import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.server.main.exception.client.BadQrCodeException;
import io.gardenerframework.camellia.authentication.server.main.qrcode.QrCodeService;
import io.gardenerframework.camellia.authentication.server.main.schema.UserAuthenticationRequestToken;
import io.gardenerframework.camellia.authentication.server.main.schema.request.QrCodeAuthenticationParameter;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.credentials.EmptyCredentials;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Validator;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2023/3/16 14:09
 */
@RequiredArgsConstructor
@SuppressWarnings("rawtypes")
public abstract class QrCodeBasedAuthenticationService<S extends QrCodeService> implements UserAuthenticationService {
    @NonNull
    private final Validator validator;
    @NonNull
    private final S service;


    @Override
    public UserAuthenticationRequestToken convert(@NonNull HttpServletRequest request, @Nullable OAuth2RequestingClient client, @NonNull Map<String, Object> context) throws AuthenticationException {
        QrCodeAuthenticationParameter parameter = new QrCodeAuthenticationParameter(request);
        parameter.validate(validator);
        try {
            Principal principal = service.getPrincipal(parameter.getCode());
            if (principal == null) {
                //没有登录名
                throw new BadQrCodeException(parameter.getCode());
            }
            return new UserAuthenticationRequestToken(
                    principal,
                    new EmptyCredentials()
            );
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalAuthenticationServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void authenticate(@NonNull UserAuthenticationRequestToken authenticationRequest, @Nullable OAuth2RequestingClient client, @NonNull User user, @NonNull Map<String, Object> context) throws AuthenticationException {
        //不需要做什么
    }
}
