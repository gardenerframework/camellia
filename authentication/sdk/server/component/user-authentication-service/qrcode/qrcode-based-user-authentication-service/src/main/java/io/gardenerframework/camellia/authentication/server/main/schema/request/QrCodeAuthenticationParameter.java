package io.gardenerframework.camellia.authentication.server.main.schema.request;

import lombok.Getter;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;

/**
 * @author zhanghan30
 * @date 2023/3/16 14:50
 */
@Getter
public class QrCodeAuthenticationParameter extends AuthenticationRequestParameter {
    @NotBlank
    private final String code;

    public QrCodeAuthenticationParameter(HttpServletRequest request) {
        super(request);
        this.code = request.getParameter("code");
    }
}
