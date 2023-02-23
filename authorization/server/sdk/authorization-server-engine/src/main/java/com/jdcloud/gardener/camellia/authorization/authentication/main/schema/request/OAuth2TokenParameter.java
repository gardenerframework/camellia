package com.jdcloud.gardener.camellia.authorization.authentication.main.schema.request;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;

/**
 * 控制token有效期
 * <p>
 * 有效期不超过设定值
 *
 * @author zhanghan30
 * @date 2022/5/20 4:11 下午
 */
public class OAuth2TokenParameter extends AbstractAuthenticationRequestParameter {
    @Positive
    @Getter(AccessLevel.NONE)
    @Nullable
    private final Long token_ttl;

    @Getter
    @Nullable
    private final Long tokenTtl;

    public OAuth2TokenParameter(HttpServletRequest request) {
        super(request);
        Long parameterValue = null;
        try {
            if (StringUtils.hasText(request.getParameter("token_ttl"))) {
                parameterValue = Long.parseLong(request.getParameter("token_ttl"));
            }
        } catch (NumberFormatException exception) {
            //do nothing
        }
        this.tokenTtl = parameterValue;
        this.token_ttl = this.tokenTtl;
    }
}
