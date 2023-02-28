package io.gardenerframework.camellia.authentication.server.configuration;

import io.gardenerframework.camellia.authentication.server.common.configuration.AuthenticationServerPathOption;
import io.gardenerframework.camellia.authentication.server.main.exception.AuthenticationServerAuthenticationExceptions;
import io.gardenerframework.camellia.authentication.server.main.exception.OAuth2ErrorCodes;
import io.gardenerframework.camellia.authentication.server.main.spring.AuthenticationEndpointAuthenticationFailureHandler;
import io.gardenerframework.camellia.authentication.server.utils.AuthenticationEndpointMatcher;
import io.gardenerframework.fragrans.api.standard.error.DefaultApiErrorConstants;
import io.gardenerframework.fragrans.api.standard.error.ServletApiErrorAttributes;
import io.gardenerframework.fragrans.api.standard.error.ServletApiErrorAttributesConfigurer;
import io.gardenerframework.fragrans.api.standard.error.configuration.RevealError;
import io.gardenerframework.fragrans.messages.EnhancedMessageSource;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Arrays;

/**
 * @author zhanghan30
 * @date 2021/12/21 10:37 下午
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
@AllArgsConstructor
@RevealError(superClasses = {
        AuthenticationServerAuthenticationExceptions.ClientSideException.class,
        AuthenticationServerAuthenticationExceptions.ServerSideException.class,
        BadCredentialsException.class,
        AccountStatusException.class
})
public class AuthenticationServerEngineSecurityConfiguration extends WebSecurityConfigurerAdapter implements ServletApiErrorAttributesConfigurer {
    private final AuthenticationServerPathOption authenticationServerPathOption;
    private final WebAuthenticationEndpointFilterConfigurer webAuthenticationEndpointFilterConfigurer;
    private final OAuth2AuthorizationServerConfigurerProxy oAuth2AuthorizationServerConfigurerProxy;
    private final AuthenticationEndpointMatcher authenticationEndpointMatcher;
    private final AuthenticationEndpointAuthenticationFailureHandler authenticationEndpointAuthenticationFailureHandler;
    private final EnhancedMessageSource messageSource;
    private final ProviderSettings providerSettings;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //添加两个配置器到builder
        http.apply(webAuthenticationEndpointFilterConfigurer);
        http.apply(oAuth2AuthorizationServerConfigurerProxy);
        http.csrf().ignoringRequestMatchers(
                //所有rest api接口
                new AntPathRequestMatcher(String.format("%s/**", authenticationServerPathOption.getRestApiContextPath()))
        );
        //默认不拦截api接口的请求
        //相关权限验证由api自己完成
        http.authorizeRequests().antMatchers(String.format("%s/**", authenticationServerPathOption.getRestApiContextPath()), authenticationServerPathOption.getWebAuthenticationErrorPage(), authenticationServerPathOption.getWebMfaChallengePage()).permitAll();
        http.authorizeRequests().anyRequest().authenticated().and()
//todo 改造完登录态再看这个
//                .addFilterAfter(accessContextCheckFilter(), SecurityContextPersistenceFilter.class)
                .logout()
//                .addLogoutHandler(accessContextLogoutHandler)
                .logoutRequestMatcher(new AntPathRequestMatcher(authenticationServerPathOption.getWebLogoutEndpoint()))
                .logoutSuccessUrl(authenticationServerPathOption.getWebLogoutPage())
                .and().exceptionHandling().authenticationEntryPoint(
                        (request, response, authException) -> {
                            //fixed 授权接口也要跳登录页
                            if (!oAuth2AuthorizationServerConfigurerProxy.getEndpointMatcher().matches(request) || oAuth2AuthorizationServerConfigurerProxy.getAuthorizationEndpointMatcher().matches(request)) {
                                response.sendRedirect(authenticationServerPathOption.getWebLoginPage());
                            } else {
                                authenticationEndpointAuthenticationFailureHandler.onAuthenticationFailure(
                                        request, response, new OAuth2AuthenticationException(
                                                new OAuth2Error(
                                                        OAuth2ErrorCodes.UNAUTHORIZED,
                                                        messageSource.getMessage(
                                                                authException,
                                                                //todo 仔细想想这里
                                                                messageSource.getMessage(DefaultApiErrorConstants.UNKNOWN_ERROR, LocaleContextHolder.getLocale()),
                                                                LocaleContextHolder.getLocale()
                                                        ),
                                                        null
                                                ),
                                                authException
                                        )
                                );
                            }
                        }
                );
        http.apply(authenticationEndpointMatcher);
    }


    @Override
    public void accept(ServletApiErrorAttributes servletApiErrorConverter) {
        //排除oauth2的所有端点
        servletApiErrorConverter.getIgnoringUrlPatterns().addAll(Arrays.asList(
                providerSettings.getAuthorizationEndpoint(),
                providerSettings.getJwkSetEndpoint(),
                providerSettings.getTokenEndpoint(),
                providerSettings.getOidcUserInfoEndpoint(),
                providerSettings.getTokenIntrospectionEndpoint(),
                providerSettings.getTokenRevocationEndpoint(),
                providerSettings.getOidcClientRegistrationEndpoint()
        ));
    }
}
