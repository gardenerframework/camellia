package com.jdcloud.gardener.camellia.authorization.authentication.main.schema.request;

import com.jdcloud.gardener.fragrans.log.annotation.LogTarget;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;

/**
 * oauth2的授权类型参数
 *
 * @author zhanghan30
 * @date 2022/5/12 6:22 下午
 */
@LogTarget("grant_type")
public class OAuth2GrantTypeParameter extends AbstractAuthenticationRequestParameter {
    /**
     * 授权类型
     * <p>
     * 这么写是兼容oauth2协议
     */
    @NotBlank
    @Getter(AccessLevel.NONE)
    private final String grant_type;

    @Getter
    private final String grantType;

    public OAuth2GrantTypeParameter(HttpServletRequest request) {
        super(request);
        this.grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        this.grant_type = this.grantType;
    }
}
