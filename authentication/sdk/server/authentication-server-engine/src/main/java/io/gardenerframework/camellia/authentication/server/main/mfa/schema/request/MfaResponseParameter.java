package io.gardenerframework.camellia.authentication.server.main.mfa.schema.request;

import io.gardenerframework.camellia.authentication.server.main.schema.request.AuthenticationRequestParameter;
import lombok.Getter;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;

/**
 * @author zhanghan30
 * @date 2021/12/28 1:18 下午
 */
public class MfaResponseParameter extends AuthenticationRequestParameter {
    @NotBlank
    @Getter
    private final String challengeId;
    /**
     * 要验证的mfa令牌
     */
    @NotBlank
    @Getter
    private final String response;

    public MfaResponseParameter(HttpServletRequest request) {
        super(request);
        this.challengeId = request.getParameter("challengeId");
        this.response = request.getParameter("response");
    }
}
