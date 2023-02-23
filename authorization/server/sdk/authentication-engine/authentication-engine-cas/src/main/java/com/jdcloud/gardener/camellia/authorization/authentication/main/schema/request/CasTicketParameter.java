package com.jdcloud.gardener.camellia.authorization.authentication.main.schema.request;

import lombok.Getter;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;

/**
 * cas登录票据请求参数
 *
 * @author zhanghan30
 * @date 2022/8/23 3:54 下午
 */
@Getter
public class CasTicketParameter extends AbstractAuthenticationRequestParameter {
    /**
     * 登录票据
     */
    @NotBlank
    private final String ticket;

    public CasTicketParameter(HttpServletRequest request) {
        super(request);
        ticket = request.getParameter("ticket");
    }
}
