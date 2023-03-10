package io.gardenerframework.camellia.authentication.server.configuration;

import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEngineComponent;
import io.gardenerframework.camellia.authentication.server.common.configuration.AuthenticationServerPathOption;
import io.gardenerframework.camellia.authentication.server.main.configuration.OAuth2ClientAuthenticationFilterRequestMatherConfigurer;
import io.gardenerframework.camellia.authentication.server.main.spring.AuthenticationEndpointAuthenticationFailureHandler;
import io.gardenerframework.camellia.authentication.server.main.spring.LoginAuthenticationRequestConverter;
import io.gardenerframework.camellia.authentication.server.main.spring.oauth2.OidcUserInfoMapper;
import io.gardenerframework.camellia.authentication.server.main.spring.oauth2.TokenEndpointAuthenticationConverterDelegate;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationEndpointConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2ClientAuthenticationConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2EndpointConfigurerRequestMatcherAccessor;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2ClientCredentialsAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2RefreshTokenAuthenticationConverter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

/**
 * ????????????{@link OAuth2AuthorizationServerConfigurer}
 * <p>
 * ??????????????????????????????????????????final???????????????????????????
 *
 * @author zhanghan30
 * @date 2022/5/12 11:04 ??????
 */
@RequiredArgsConstructor
@AuthenticationServerEngineComponent
public class OAuth2AuthorizationServerConfigurerProxy extends AuthenticationServerEngineSecurityConfigurer {
    private final AuthenticationServerPathOption authenticationServerPathOption;
    private final OAuth2AuthorizationConsentService oAuth2AuthorizationConsentService;
    private final OAuth2AuthorizationService oAuth2AuthorizationService;
    private final OidcUserInfoMapper oidcUserInfoMapper;
    private final AuthenticationEndpointAuthenticationFailureHandler authenticationEndpointAuthenticationFailureHandler;
    private final LoginAuthenticationRequestConverter loginAuthenticationRequestConverter;
    private final OAuth2AuthorizationServerConfigurer<HttpSecurity> oAuth2AuthorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer<>();
    private final Collection<OAuth2ClientAuthenticationFilterRequestMatherConfigurer> clientAuthenticationFilterRequestMatherConfigurers;
    private final TokenEndpointAuthenticationConverterDelegate tokenEndpointAuthenticationConverterDelegate;
    private OAuth2AuthorizationEndpointConfigurer oAuth2AuthorizationEndpointConfigurer;
    @Setter
    private OAuth2ClientAuthenticationConfigurer oAuth2ClientAuthenticationConfigurer;

    /**
     * ???????????????????????????????????????{@link OAuth2AuthorizationServerConfigurer}
     *
     * @param builder builder
     * @throws Exception ??????
     */
    @Override
    public void init(HttpSecurity builder) throws Exception {
        //???????????????oidc????????????????????????
        oAuth2AuthorizationServerConfigurer.oidc(
                oidcConfigurer ->
                        oidcConfigurer.userInfoEndpoint(
                                oidcUserInfoEndpointConfigurer -> oidcUserInfoEndpointConfigurer.userInfoMapper(oidcUserInfoMapper)
                        )
        );
        //??????????????????
        //???????????????????????????bean??????provider setting????????????????????????????????????????????????
        builder.apply(oAuth2AuthorizationServerConfigurer);
        //?????????Bearer token?????????
        //????????????builder???????????????apply??????????????????
        //??????????????????????????????????????????????????????????????????
        builder.oauth2ResourceServer().jwt();
        //???provider setting?????????
        Field configurersField = oAuth2AuthorizationServerConfigurer.getClass().getDeclaredField("configurers");
        configurersField.setAccessible(true);
        Map<?, ?> configurers = (Map<?, ?>) configurersField.get(oAuth2AuthorizationServerConfigurer);
        this.oAuth2AuthorizationEndpointConfigurer = (OAuth2AuthorizationEndpointConfigurer) configurers.get(OAuth2AuthorizationEndpointConfigurer.class);
        configurersField.setAccessible(false);
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        //oauth2??????????????????csrf
        builder.csrf().ignoringRequestMatchers(oAuth2AuthorizationServerConfigurer.getEndpointsMatcher());
        //???????????????
        oAuth2AuthorizationServerConfigurer.authorizationEndpoint(
                oAuth2AuthorizationEndpointConfigurer -> oAuth2AuthorizationEndpointConfigurer.consentPage(authenticationServerPathOption.getOAuth2AuthorizationConsentPage())
        );
        oAuth2AuthorizationServerConfigurer.authorizationService(oAuth2AuthorizationService);
        oAuth2AuthorizationServerConfigurer.authorizationConsentService(oAuth2AuthorizationConsentService);
        oAuth2AuthorizationServerConfigurer.tokenEndpoint(oAuth2TokenEndpointConfigurer -> {
//            oAuth2TokenEndpointConfigurer
//                    .authenticationProvider(authenticationContextAuthenticator);
//                    //?????????spring?????????????????????????????????????????????????????????????????????????????????????????????ProviderManager????????????????????????
//                    //????????????????????????2??????????????????????????????????????????2???
            oAuth2TokenEndpointConfigurer.errorResponseHandler(authenticationEndpointAuthenticationFailureHandler);
            tokenEndpointAuthenticationConverterDelegate.addConverter(
                    //??????3??????
                    new OAuth2AuthorizationCodeAuthenticationConverter(),
                    new OAuth2RefreshTokenAuthenticationConverter(),
                    new OAuth2ClientCredentialsAuthenticationConverter(),
                    //???????????????
                    loginAuthenticationRequestConverter
            );
            oAuth2TokenEndpointConfigurer.accessTokenRequestConverter(tokenEndpointAuthenticationConverterDelegate);
        });

        OAuth2AuthorizationServerConfigurerProxy thisConfigurer = this;
        oAuth2AuthorizationServerConfigurer.clientAuthentication(
                configurer -> {
                    thisConfigurer.setOAuth2ClientAuthenticationConfigurer(configurer);
                    //added ???OAuth2ClientAuthentication?????????????????????
                    configurer.errorResponseHandler(authenticationEndpointAuthenticationFailureHandler);
                }
        );
        if (!CollectionUtils.isEmpty(clientAuthenticationFilterRequestMatherConfigurers)) {
            try {
                Field requestMatcherField = oAuth2ClientAuthenticationConfigurer.getClass().getDeclaredField("requestMatcher");
                requestMatcherField.setAccessible(true);
                RequestMatcher matcher = (RequestMatcher) FieldUtils.readField(requestMatcherField, oAuth2ClientAuthenticationConfigurer, true);
                //todo ??????????????????????????????????????????????????????????????????????????????rest api????????????client id???secret
                requestMatcherField.setAccessible(false);
            } catch (NoSuchFieldException | IllegalAccessException exception) {
                throw new IllegalStateException(exception);
            }
        }

    }


    /**
     * ????????????????????????????????????
     *
     * @return ??????
     */
    public RequestMatcher getEndpointMatcher() {
        return this.oAuth2AuthorizationServerConfigurer.getEndpointsMatcher();
    }

    /**
     * ???????????????????????????
     *
     * @return ??????
     */
    public RequestMatcher getAuthorizationEndpointMatcher() {
        return OAuth2EndpointConfigurerRequestMatcherAccessor.getAuthorizationEndpointRequestMatcher(oAuth2AuthorizationEndpointConfigurer);
    }
}
