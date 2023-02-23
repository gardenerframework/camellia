package io.gardenerframework.camellia.authentication.server.main.schema.request;

import io.gardenerframework.camellia.authentication.server.main.schema.request.constraints.AuthenticationTypeSupported;
import lombok.Getter;
import lombok.NonNull;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ZhangHan
 * @date 2022/5/11 12:23
 */
public class AuthenticationTypeParameter extends AuthenticationRequestParameter {
    @AuthenticationTypeSupported
    @Getter
    @NonNull
    private final String authenticationType;

    public AuthenticationTypeParameter(HttpServletRequest request) {
        super(request);
        this.authenticationType = request.getParameter("authenticationType");
    }
}
