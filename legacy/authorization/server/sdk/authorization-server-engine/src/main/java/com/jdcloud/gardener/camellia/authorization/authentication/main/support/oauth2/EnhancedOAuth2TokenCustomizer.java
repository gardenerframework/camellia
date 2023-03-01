package com.jdcloud.gardener.camellia.authorization.authentication.main.support.oauth2;

import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.request.OAuth2TokenParameter;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2TokenType;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * 完成token的客制化功能代理
 *
 * @author ZhangHan
 * @date 2022/5/21 1:08
 */
@Component
public class EnhancedOAuth2TokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {
    @Override
    public void customize(JwtEncodingContext context) {
        OAuth2TokenType tokenType = context.getTokenType();
        if (tokenType != null) {
            //是个令牌
            //通过参数覆盖ttl
            overwriteJwtEncodedTokenTtl(tokenType, context.getClaims(), context.getRegisteredClient());
        }
    }

    private void overwriteJwtEncodedTokenTtl(OAuth2TokenType oAuth2TokenType, JwtClaimsSet.Builder claims, RegisteredClient registeredClient) {
        OAuth2TokenParameter oAuth2TokenParameter = (OAuth2TokenParameter) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()).getAttribute(OAuth2TokenParameter.class.getName(), RequestAttributes.SCOPE_REQUEST);
        if (oAuth2TokenParameter != null) {
            Long tokenTtl = oAuth2TokenParameter.getTokenTtl();
            if (tokenTtl != null) {
                //客户端设定的token ttl
                Duration clientAuthorizedTokenTtl = registeredClient.getTokenSettings().getAccessTokenTimeToLive();
                if (OAuth2TokenType.REFRESH_TOKEN.equals(oAuth2TokenType)) {
                    //这个貌似没有出现过
                    clientAuthorizedTokenTtl = registeredClient.getTokenSettings().getRefreshTokenTimeToLive();
                }
                if (tokenTtl <= clientAuthorizedTokenTtl.getSeconds()) {
                    //完成覆盖
                    Instant now = Instant.now();
                    claims.issuedAt(now);
                    claims.expiresAt(now.plus(Duration.ofSeconds(tokenTtl)));
                }
            }
        }
    }

    /**
     * 客制化并返回一个新的refresh token
     *
     * @param refreshToken 旧token
     * @return 新token(如果没有改动就返回和旧的一样)
     */
    public OAuth2RefreshToken customizeRefreshToken(@Nullable OAuth2RefreshToken refreshToken) {
        if (refreshToken == null) {
            return null;
        }
        return overwriteRefreshTokenTtl(refreshToken);
    }

    /**
     * 覆盖refresh token的ttl
     *
     * @param refreshToken 旧token
     * @return 新token
     */
    private OAuth2RefreshToken overwriteRefreshTokenTtl(OAuth2RefreshToken refreshToken) {
        OAuth2TokenParameter oAuth2TokenParameter = (OAuth2TokenParameter) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()).getAttribute(OAuth2TokenParameter.class.getName(), RequestAttributes.SCOPE_REQUEST);
        if (oAuth2TokenParameter == null || oAuth2TokenParameter.getTokenTtl() == null) {
            return refreshToken;
        }
        Instant now = Instant.now();
        Instant issuedAt = refreshToken.getIssuedAt();
        if (issuedAt == null) {
            issuedAt = now;
        }
        Instant expiresAt = refreshToken.getExpiresAt();
        Duration ttl = expiresAt == null ? Duration.ofSeconds(oAuth2TokenParameter.getTokenTtl()) : Duration.between(issuedAt, expiresAt);
        if (ttl.getSeconds() > oAuth2TokenParameter.getTokenTtl()) {
            refreshToken = new OAuth2RefreshToken(refreshToken.getTokenValue(), issuedAt, issuedAt.plus(Duration.ofSeconds(oAuth2TokenParameter.getTokenTtl())));
        }
        return refreshToken;
    }
}
