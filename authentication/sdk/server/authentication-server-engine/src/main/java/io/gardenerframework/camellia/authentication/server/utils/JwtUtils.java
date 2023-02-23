package io.gardenerframework.camellia.authentication.server.utils;

import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;

/**
 * spring真的是很小气，都做开源了，唉
 * <p>
 * 让我点一下能死么
 *
 * @author zhanghan30
 * @date 2022/4/19 9:40 下午
 * @see org.springframework.security.oauth2.server.authorization.authentication.JwtUtils
 */
public abstract class JwtUtils {

    private JwtUtils() {
    }

    public static JwsHeader.Builder headers() {
        return JwsHeader.with(SignatureAlgorithm.RS256);
    }

    public static JwtClaimsSet.Builder accessTokenClaims(RegisteredClient registeredClient,
                                                         String issuer, String subject, Set<String> authorizedScopes) {

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(registeredClient.getTokenSettings().getAccessTokenTimeToLive());

        // @formatter:off
        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder();
        if (StringUtils.hasText(issuer)) {
            claimsBuilder.issuer(issuer);
        }
        claimsBuilder
                .subject(subject)
                .audience(Collections.singletonList(registeredClient.getClientId()))
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .notBefore(issuedAt);
        if (!CollectionUtils.isEmpty(authorizedScopes)) {
            claimsBuilder.claim(OAuth2ParameterNames.SCOPE, authorizedScopes);
        }
        // @formatter:on

        return claimsBuilder;
    }

    public static JwtClaimsSet.Builder idTokenClaims(RegisteredClient registeredClient,
                                                     String issuer, String subject, String nonce) {

        Instant issuedAt = Instant.now();
        // TODO Allow configuration for ID Token time-to-live
        Instant expiresAt = issuedAt.plus(registeredClient.getTokenSettings().getAccessTokenTimeToLive());

        // @formatter:off
        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder();
        if (StringUtils.hasText(issuer)) {
            claimsBuilder.issuer(issuer);
        }
        claimsBuilder
                .subject(subject)
                .audience(Collections.singletonList(registeredClient.getClientId()))
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .claim(IdTokenClaimNames.AZP, registeredClient.getClientId());
        if (StringUtils.hasText(nonce)) {
            claimsBuilder.claim(IdTokenClaimNames.NONCE, nonce);
        }
        // TODO Add 'auth_time' claim
        // @formatter:on

        return claimsBuilder;
    }

}
