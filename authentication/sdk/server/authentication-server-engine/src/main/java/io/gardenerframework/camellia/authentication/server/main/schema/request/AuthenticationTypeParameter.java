package io.gardenerframework.camellia.authentication.server.main.schema.request;

import io.gardenerframework.camellia.authentication.server.main.schema.request.constraints.AuthenticationTypeSupported;
import lombok.Getter;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ZhangHan
 * @date 2022/5/11 12:23
 */
public class AuthenticationTypeParameter extends AuthenticationRequestParameter {
    @AuthenticationTypeSupported(
            //当提交认证时，肯定有保留的比如mfa的认证类型
            ignorePreserved = false,
            //当前是要给认证接口用的
            endpointType = AuthenticationTypeSupported.EndpointType.Authentication
    )
    @Getter
    private final String authenticationType;

    public AuthenticationTypeParameter(HttpServletRequest request) {
        super(request);
        this.authenticationType = request.getParameter("authenticationType");
    }
}
