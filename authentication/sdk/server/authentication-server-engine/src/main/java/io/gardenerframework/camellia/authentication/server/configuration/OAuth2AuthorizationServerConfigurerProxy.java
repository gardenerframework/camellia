package io.gardenerframework.camellia.authentication.server.configuration;

import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEngineComponent;
import io.gardenerframework.camellia.authentication.server.common.configuration.AuthenticationServerPathOption;
import io.gardenerframework.camellia.authentication.server.main.OidcUserInfoMapper;
import io.gardenerframework.camellia.authentication.server.main.configuration.OAuth2ClientAuthenticationFilterRequestMatherConfigurer;
import io.gardenerframework.camellia.authentication.server.main.spring.AuthenticationEndpointAuthenticationFailureHandler;
import io.gardenerframework.camellia.authentication.server.main.spring.LoginAuthenticationRequestConverter;
import io.gardenerframework.camellia.authentication.server.main.spring.support.oauth2.TokenEndpointAuthenticationConverterDelegate;
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
 * 负责代理{@link OAuth2AuthorizationServerConfigurer}
 * <p>
 * 之所以要代理是因为那个玩意是final类，没有办法子类化
 *
 * @author zhanghan30
 * @date 2022/5/12 11:04 下午
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
     * 自当前代理初始化的时候设置{@link OAuth2AuthorizationServerConfigurer}
     *
     * @param builder builder
     * @throws Exception 异常
     */
    @Override
    public void init(HttpSecurity builder) throws Exception {
        //首先要设置oidc的用户映射解析器
        oAuth2AuthorizationServerConfigurer.oidc(
                oidcConfigurer ->
                        oidcConfigurer.userInfoEndpoint(
                                oidcUserInfoEndpointConfigurer -> oidcUserInfoEndpointConfigurer.userInfoMapper(oidcUserInfoMapper)
                        )
        );
        //初始化配置器
        //这个初始化包含了从bean中找provider setting并设置为共享对象，不需要再设置了
        builder.apply(oAuth2AuthorizationServerConfigurer);
        //添加对Bearer token的支持
        //这里其实builder去初始化和apply了一个默认的
        //如果后续还需要对资源服务器有定开，还得改这里
        builder.oauth2ResourceServer().jwt();
        //将provider setting给到它
        Field configurersField = oAuth2AuthorizationServerConfigurer.getClass().getDeclaredField("configurers");
        configurersField.setAccessible(true);
        Map<?, ?> configurers = (Map<?, ?>) configurersField.get(oAuth2AuthorizationServerConfigurer);
        this.oAuth2AuthorizationEndpointConfigurer = (OAuth2AuthorizationEndpointConfigurer) configurers.get(OAuth2AuthorizationEndpointConfigurer.class);
        configurersField.setAccessible(false);
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        //oauth2的路径不需要csrf
        builder.csrf().ignoringRequestMatchers(oAuth2AuthorizationServerConfigurer.getEndpointsMatcher());
        //配置批准页
        oAuth2AuthorizationServerConfigurer.authorizationEndpoint(
                oAuth2AuthorizationEndpointConfigurer -> oAuth2AuthorizationEndpointConfigurer.consentPage(authenticationServerPathOption.getOAuth2AuthorizationConsentPage())
        );
        oAuth2AuthorizationServerConfigurer.authorizationService(oAuth2AuthorizationService);
        oAuth2AuthorizationServerConfigurer.authorizationConsentService(oAuth2AuthorizationConsentService);
        oAuth2AuthorizationServerConfigurer.tokenEndpoint(oAuth2TokenEndpointConfigurer -> {
//            oAuth2TokenEndpointConfigurer
//                    .authenticationProvider(authenticationContextAuthenticator);
//                    //他妈的spring简直有毒，这里如果设置了这个，首先授权码的验证器不会生效，其次ProviderManager中已经自动注册了
//                    //导致认证会被执行2次，从而所有认证错误都被记录2次
            oAuth2TokenEndpointConfigurer.errorResponseHandler(authenticationEndpointAuthenticationFailureHandler);
            tokenEndpointAuthenticationConverterDelegate.addConverter(
                    //基本3幻神
                    new OAuth2AuthorizationCodeAuthenticationConverter(),
                    new OAuth2RefreshTokenAuthenticationConverter(),
                    new OAuth2ClientCredentialsAuthenticationConverter(),
                    //转换器代理
                    loginAuthenticationRequestConverter
            );
            oAuth2TokenEndpointConfigurer.accessTokenRequestConverter(tokenEndpointAuthenticationConverterDelegate);
        });

        OAuth2AuthorizationServerConfigurerProxy thisConfigurer = this;
        oAuth2AuthorizationServerConfigurer.clientAuthentication(
                configurer -> {
                    thisConfigurer.setOAuth2ClientAuthenticationConfigurer(configurer);
                    //added 将OAuth2ClientAuthentication的错误处理归口
                    configurer.errorResponseHandler(authenticationEndpointAuthenticationFailureHandler);
                }
        );
        if (!CollectionUtils.isEmpty(clientAuthenticationFilterRequestMatherConfigurers)) {
            try {
                Field requestMatcherField = oAuth2ClientAuthenticationConfigurer.getClass().getDeclaredField("requestMatcher");
                requestMatcherField.setAccessible(true);
                RequestMatcher matcher = (RequestMatcher) FieldUtils.readField(requestMatcherField, oAuth2ClientAuthenticationConfigurer, true);
                //todo 在这里实行对客户端认证过滤器匹配路径的设置，从而使的rest api能够要求client id和secret
                requestMatcherField.setAccessible(false);
            } catch (NoSuchFieldException | IllegalAccessException exception) {
                throw new IllegalStateException(exception);
            }
        }

    }


    /**
     * 返回授权服务器的所有地址
     *
     * @return 地址
     */
    public RequestMatcher getEndpointMatcher() {
        return this.oAuth2AuthorizationServerConfigurer.getEndpointsMatcher();
    }

    /**
     * 返回授权接口的地址
     *
     * @return 地址
     */
    public RequestMatcher getAuthorizationEndpointMatcher() {
        return OAuth2EndpointConfigurerRequestMatcherAccessor.getAuthorizationEndpointRequestMatcher(oAuth2AuthorizationEndpointConfigurer);
    }
}
