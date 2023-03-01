package com.jdcloud.gardener.camellia.authorization.authentication.main.schema.request;

import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.request.constraints.AuthenticationTypeSupported;
import lombok.Getter;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ZhangHan
 * @date 2022/5/11 12:23
 */
public class AuthenticationTypeParameter extends AbstractAuthenticationRequestParameter {
    @AuthenticationTypeSupported
    @Getter
    private final String authenticationType;

    public AuthenticationTypeParameter(HttpServletRequest request) {
        super(request);
        this.authenticationType = request.getParameter("authenticationType");
    }
}
