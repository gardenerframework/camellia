package com.jdcloud.gardener.camellia.authorization.authentication.main.schema.request;

import lombok.Getter;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;

/**
 * 二维码登录请求参数
 *
 * @author zhanghan30
 * @date 2022/7/29 7:20 下午
 */
@Getter
public class QrCodeParameter extends AbstractAuthenticationRequestParameter {
    /**
     * 已经存储了用户信息的token
     */
    @NotBlank
    private final String token;

    public QrCodeParameter(HttpServletRequest request) {
        super(request);
        this.token = request.getParameter("token");
    }
}
