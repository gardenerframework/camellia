package io.gardenerframework.camellia.authentication.server.main.spring.oauth2;

import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEngineComponent;
import io.gardenerframework.camellia.authentication.server.main.schema.OAuth2ClientUserAuthenticationToken;
import io.gardenerframework.camellia.authentication.server.main.schema.UserAuthenticatedAuthentication;
import io.gardenerframework.camellia.authentication.server.main.utils.JwtUtils;
import io.gardenerframework.fragrans.log.GenericLoggerStaticAccessor;
import io.gardenerframework.fragrans.log.common.schema.reason.NotFound;
import io.gardenerframework.fragrans.log.schema.content.GenericBasicLogContent;
import io.gardenerframework.fragrans.log.schema.details.Detail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.security.Principal;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * ???????????????????????????token ????????????????????????????????????spring?????????????????????????????????
 * <p>
 * ???????????????????????????OAuth2??????????????????????????????????????????????????????????????????
 *
 * @author zhanghan30
 * @date 2022/4/19 9:06 ??????
 * @see OAuth2AuthorizationCodeAuthenticationProvider
 */
@Slf4j
@AllArgsConstructor
@AuthenticationServerEngineComponent
public class UserAuthenticationOAuth2AccessTokenGranter {
    private static final StringKeyGenerator DEFAULT_REFRESH_TOKEN_GENERATOR =
            new Base64StringKeyGenerator(Base64.getUrlEncoder().withoutPadding(), 96);
    private final OAuth2AuthorizationService oAuth2AuthorizationService;
    private final JwtEncoder jwtEncoder;
    private final ProviderSettings providerSettings;
    private final OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer;

    /**
     * ???spring ??????
     *
     * @param clientUserAuthentication ?????????????????????????????????
     * @return ???????????????
     */
    private OAuth2ClientAuthenticationToken getAuthenticatedClientElseThrowInvalidClient(OAuth2ClientUserAuthenticationToken clientUserAuthentication) {
        OAuth2ClientAuthenticationToken clientPrincipal = clientUserAuthentication.getPrincipal();
        if (clientPrincipal != null && clientPrincipal.isAuthenticated()) {
            return clientPrincipal;
        }
        //???????????????????????????
        GenericLoggerStaticAccessor.basicLogger().info(
                log,
                GenericBasicLogContent.builder()
                        .what(OAuth2ClientAuthenticationToken.class)
                        .how(new NotFound())
                        .detail(new Detail() {
                            private final OAuth2ClientAuthenticationToken client = clientPrincipal;
                        }).build(),
                null
        );
        throw new OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT);
    }

    /**
     * ??????access token
     * <p>
     * ????????????????????????????????????????????????
     *
     * @param clientAuthentication ???????????????
     * @param userAuthentication   ????????????
     * @return ????????????
     */
    private JwtOAuth2AccessToken grantAccessToken(
            OAuth2ClientUserAuthenticationToken clientAuthentication,
            UserAuthenticatedAuthentication userAuthentication
    ) {
        OAuth2ClientAuthenticationToken clientPrincipal = getAuthenticatedClientElseThrowInvalidClient(clientAuthentication);
        String issuer = this.providerSettings != null ? this.providerSettings.getIssuer() : null;
        Set<String> authorizedScopes = clientAuthentication.getScopes();
        JwsHeader.Builder headersBuilder = JwtUtils.headers();
        JwtClaimsSet.Builder claimsBuilder = JwtUtils.accessTokenClaims(
                Objects.requireNonNull(clientPrincipal.getRegisteredClient()),
                issuer,
                //sub?????????id
                userAuthentication.getUser().getId(),
                authorizedScopes
        );
        // @formatter:off
        JwtEncodingContext context = JwtEncodingContext.with(headersBuilder, claimsBuilder)
                .registeredClient(clientPrincipal.getRegisteredClient())
                .principal(userAuthentication)
                .authorizedScopes(authorizedScopes)
                .tokenType(OAuth2TokenType.ACCESS_TOKEN)
                .authorizationGrantType(clientAuthentication.getGrantType())
                .authorizationGrant(clientAuthentication)
                .build();
        // @formatter:on
        //????????????
        this.tokenCustomizer.customize(context);
        JwsHeader headers = context.getHeaders().build();
        JwtClaimsSet claims = context.getClaims().build();
        Jwt jwtAccessToken = this.jwtEncoder.encode(JwtEncoderParameters.from(headers, claims));

        return new JwtOAuth2AccessToken(jwtAccessToken, OAuth2AccessToken.TokenType.BEARER,
                jwtAccessToken.getTokenValue(), jwtAccessToken.getIssuedAt(),
                jwtAccessToken.getExpiresAt(), authorizedScopes);
    }

    /**
     * ??????????????????
     *
     * @param clientAuthentication ??????????????????
     * @return ????????????
     */
    @Nullable
    private OAuth2RefreshToken grantRefreshToken(OAuth2ClientUserAuthenticationToken clientAuthentication) {
        OAuth2ClientAuthenticationToken clientPrincipal = getAuthenticatedClientElseThrowInvalidClient(clientAuthentication);
        OAuth2RefreshToken refreshToken = null;
        if (clientPrincipal.getRegisteredClient().getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN) &&
                // Do not issue refresh token to public client
                !clientPrincipal.getClientAuthenticationMethod().equals(ClientAuthenticationMethod.NONE)) {
            Duration refreshTokenTimeToLive = clientPrincipal.getRegisteredClient().getTokenSettings().getRefreshTokenTimeToLive();
            refreshToken = generateRefreshToken(refreshTokenTimeToLive);
        }
        return refreshToken;
    }

    /**
     * ??????id token
     *
     * @param clientAuthentication ???????????????
     * @param userAuthentication   ????????????
     * @return id token
     */
    @Nullable
    private OidcIdToken grantIdToken(OAuth2ClientUserAuthenticationToken clientAuthentication, UserAuthenticatedAuthentication userAuthentication) {
        OAuth2ClientAuthenticationToken clientPrincipal = getAuthenticatedClientElseThrowInvalidClient(clientAuthentication);
        Jwt jwtIdToken = null;
        String issuer = this.providerSettings != null ? this.providerSettings.getIssuer() : null;
        if (clientAuthentication.getScopes().contains(OidcScopes.OPENID)) {
            String nonce = (String) clientAuthentication.getAdditionalParameters().get(OidcParameterNames.NONCE);
            JwsHeader.Builder headersBuilder = JwtUtils.headers();
            JwtClaimsSet.Builder claimsBuilder = JwtUtils.idTokenClaims(
                    clientPrincipal.getRegisteredClient(),
                    issuer,
                    userAuthentication.getUser().getId(),
                    nonce
            );
            // @formatter:off
            JwtEncodingContext context = JwtEncodingContext.with(headersBuilder, claimsBuilder)
                    .registeredClient(clientPrincipal.getRegisteredClient())
                    .principal(userAuthentication)
                    .authorizedScopes(clientAuthentication.getScopes())
                    .tokenType(new OAuth2TokenType(OidcParameterNames.ID_TOKEN))
                    .authorizationGrantType(clientAuthentication.getGrantType())
                    .authorizationGrant(clientAuthentication)
                    .build();
            // @formatter:on
            this.tokenCustomizer.customize(context);
            JwsHeader headers = context.getHeaders().build();
            JwtClaimsSet claims = context.getClaims().build();
            jwtIdToken = this.jwtEncoder.encode(JwtEncoderParameters.from(headers, claims));
        }

        if (jwtIdToken != null) {
            return new OidcIdToken(jwtIdToken.getTokenValue(), jwtIdToken.getIssuedAt(),
                    jwtIdToken.getExpiresAt(), jwtIdToken.getClaims());
        } else {
            return null;
        }
    }

    /**
     * ????????????oauth2???????????????
     *
     * @param accessToken  ????????????
     * @param refreshToken ????????????
     * @param idToken      id??????
     * @return OAuth2??????
     */
    private OAuth2Authorization createAuthorization(
            OAuth2ClientUserAuthenticationToken clientAuthentication,
            UserAuthenticatedAuthentication userAuthentication,
            JwtOAuth2AccessToken accessToken,
            @Nullable OAuth2RefreshToken refreshToken,
            @Nullable OidcIdToken idToken

    ) {
        OAuth2ClientAuthenticationToken clientPrincipal = getAuthenticatedClientElseThrowInvalidClient(clientAuthentication);
        OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.withRegisteredClient(clientPrincipal.getRegisteredClient())
                //fixed ????????????????????????id
                .principalName(userAuthentication.getUser().getId())
                .authorizationGrantType(clientAuthentication.getGrantType())
                .attribute(Principal.class.getName(), userAuthentication)
                //fixed refresh token auth provider ?????????null????????????????????????
                .attribute(OAuth2Authorization.AUTHORIZED_SCOPE_ATTRIBUTE_NAME, clientAuthentication.getScopes() == null ? Collections.emptySet() : clientAuthentication.getScopes())
                .id(UUID.randomUUID().toString())
                //?????????????????????????????????????????????
                .token(new OAuth2AccessToken(accessToken.getTokenType(), accessToken.getTokenValue(), accessToken.getIssuedAt(), accessToken.getExpiresAt(), accessToken.getScopes()),
                        (metadata) ->
                                metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, accessToken.getJwtAccessToken().getClaims())
                );
        if (refreshToken != null) {
            authorizationBuilder.refreshToken(refreshToken);
        }
        if (idToken != null) {
            authorizationBuilder
                    .token(idToken,
                            (metadata) ->
                                    metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, idToken.getClaims()));
        }
        return authorizationBuilder.build();
    }

    /**
     * ???????????????????????????
     *
     * @param clientAuthentication ???????????????
     * @param userAuthentication   ????????????
     * @return ????????????
     */
    public OAuth2AccessTokenAuthenticationToken createOAuth2AccessTokenAuthenticationToken(
            OAuth2ClientUserAuthenticationToken clientAuthentication,
            UserAuthenticatedAuthentication userAuthentication
    ) {
        OAuth2ClientAuthenticationToken clientPrincipal = getAuthenticatedClientElseThrowInvalidClient(clientAuthentication);
        JwtOAuth2AccessToken accessToken = grantAccessToken(clientAuthentication, userAuthentication);
        OAuth2RefreshToken refreshToken = grantRefreshToken(clientAuthentication);
        OidcIdToken idToken = grantIdToken(clientAuthentication, userAuthentication);
        OAuth2Authorization authorization = createAuthorization(clientAuthentication, userAuthentication, accessToken, refreshToken, idToken);
        this.oAuth2AuthorizationService.save(authorization);
        Map<String, Object> additionalParameters = Collections.emptyMap();
        if (idToken != null) {
            additionalParameters = new HashMap<>();
            additionalParameters.put(OidcParameterNames.ID_TOKEN, idToken.getTokenValue());
        }
        return new OAuth2AccessTokenAuthenticationToken(Objects.requireNonNull(clientPrincipal.getRegisteredClient()), clientAuthentication, accessToken, refreshToken, additionalParameters);
    }

    /**
     * ??????refresh token
     *
     * @param tokenTimeToLive refresh token???ttl
     * @return ????????????
     */
    private OAuth2RefreshToken generateRefreshToken(Duration tokenTimeToLive) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(tokenTimeToLive);
        return new OAuth2RefreshToken(DEFAULT_REFRESH_TOKEN_GENERATOR.generateKey(), issuedAt, expiresAt);
    }

    public static class JwtOAuth2AccessToken extends OAuth2AccessToken {
        @Getter
        private final Jwt jwtAccessToken;

        public JwtOAuth2AccessToken(Jwt jwtAccessToken, TokenType tokenType, String tokenValue, Instant issuedAt, Instant expiresAt) {
            super(tokenType, tokenValue, issuedAt, expiresAt);
            this.jwtAccessToken = jwtAccessToken;
        }

        public JwtOAuth2AccessToken(Jwt jwtAccessToken, TokenType tokenType, String tokenValue, Instant issuedAt, Instant expiresAt, Set<String> scopes) {
            super(tokenType, tokenValue, issuedAt, expiresAt, scopes);
            this.jwtAccessToken = jwtAccessToken;
        }
    }
}
