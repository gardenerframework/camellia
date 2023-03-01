package com.jdcloud.gardener.camellia.authorization.authentication.main.schema;

import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 基于OAuth2标准协议的，客户端要求进行UserAuthentication的认证请求
 *
 * @author zhanghan30
 * @date 2022/5/12 5:55 下午
 */
public class OAuth2ClientUserAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {
    /**
     * 当前访问所需的范围
     */
    @Getter
    private final Set<String> scopes;

    public OAuth2ClientUserAuthenticationToken(AuthorizationGrantType authorizationGrantType, OAuth2ClientAuthenticationToken clientPrincipal, @Nullable Map<String, Object> additionalParameters, Set<String> scopes) {
        super(authorizationGrantType, clientPrincipal, additionalParameters);
        this.scopes = scopes;
    }

    public String getClientId() {
        return Objects.requireNonNull(getPrincipal().getRegisteredClient()).getClientId();
    }

    public RegisteredClient getRegisteredClient() {
        return Objects.requireNonNull(getPrincipal().getRegisteredClient());
    }

    @Override
    public OAuth2ClientAuthenticationToken getPrincipal() {
        return (OAuth2ClientAuthenticationToken) super.getPrincipal();
    }
}
