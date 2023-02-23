package io.gardenerframework.camellia.authentication.server.main.schema.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;

/**
 * oauth2的授权类型参数
 *
 * @author zhanghan30
 * @date 2022/5/12 6:22 下午
 */
public class OAuth2GrantTypeParameter extends AuthenticationRequestParameter {
    /**
     * 授权类型
     * <p>
     * 这么写是兼容oauth2协议
     */
    @NotBlank
    @Getter(AccessLevel.NONE)
    @NonNull
    private final String grant_type;

    @Getter
    @NonNull
    private final String grantType;

    public OAuth2GrantTypeParameter(HttpServletRequest request) {
        super(request);
        this.grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        this.grant_type = this.grantType;
    }
}
