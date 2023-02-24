package io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.request;

import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.request.AbstractAuthenticationRequestParameter;
import lombok.Getter;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;

/**
 * @author zhanghan30
 * @date 2021/12/28 1:18 下午
 */
public class MfaResponseParameter extends AbstractAuthenticationRequestParameter {
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
