package io.gardenerframework.camellia.authentication.server.main.schema.request;

import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;

/**
 * @author ZhangHan
 * @date 2022/11/8 20:39
 */
@Getter
@Setter
public class OAuth2AuthorizationCodeParameter extends AuthenticationRequestParameter {
    /**
     * 授权码，用于sns软件交换用户信息
     */
    @NotBlank
    private String code;
    /**
     * sns网站回传的state
     * <p>
     * 这是一个必须使用的参数，用于防止CSRF攻击
     */
    @NotBlank
    private String state;

    public OAuth2AuthorizationCodeParameter(HttpServletRequest request) {
        super(request);
        this.code = request.getParameter("code");
        this.state = request.getParameter("state");
    }
}
