package com.jdcloud.gardener.camellia.authorization.authentication.configuration;

import com.jdcloud.gardener.camellia.authorization.authentication.AuthorizationServerSecurityPackage;
import com.jdcloud.gardener.camellia.authorization.authentication.main.AuthenticationEndpointAuthenticationFailureHandler;
import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.AuthorizationServerAuthenticationExceptions;
import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.OAuth2ErrorCodes;
import com.jdcloud.gardener.camellia.authorization.authentication.utils.AuthenticationEndpointMatcher;
import com.jdcloud.gardener.camellia.authorization.challenge.exception.client.ChallengeException;
import com.jdcloud.gardener.camellia.authorization.common.configuration.AuthorizationServerPathOption;
import com.jdcloud.gardener.fragrans.api.standard.error.DefaultApiErrorConstants;
import com.jdcloud.gardener.fragrans.api.standard.error.ServletApiErrorAttributes;
import com.jdcloud.gardener.fragrans.api.standard.error.ServletApiErrorAttributesConfigurer;
import com.jdcloud.gardener.fragrans.api.standard.error.configuration.RevealError;
import com.jdcloud.gardener.fragrans.messages.EnhancedMessageSource;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
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
 * @date 2021/12/21 10:37 ??????
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
@AllArgsConstructor
@ComponentScan(basePackageClasses = AuthorizationServerSecurityPackage.class)
@RevealError(superClasses = {
        AuthorizationServerAuthenticationExceptions.ClientSideException.class,
        AuthorizationServerAuthenticationExceptions.ServerSideException.class,
        BadCredentialsException.class,
        AccountStatusException.class,
        ChallengeException.class
})
public class AuthorizationServerEngineSecurityConfiguration extends WebSecurityConfigurerAdapter implements ServletApiErrorAttributesConfigurer {
    private final AuthorizationServerPathOption authorizationServerPathOption;
    private final WebAuthenticationEndpointFilterConfigurer webAuthenticationEndpointFilterConfigurer;
    private final OAuth2AuthorizationServerConfigurerProxy oAuth2AuthorizationServerConfigurerProxy;
    private final AuthenticationEndpointMatcher authenticationEndpointMatcher;
    private final AuthenticationEndpointAuthenticationFailureHandler authenticationEndpointAuthenticationFailureHandler;
    private final EnhancedMessageSource messageSource;
    private final ProviderSettings providerSettings;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //????????????????????????builder
        http.apply(webAuthenticationEndpointFilterConfigurer);
        http.apply(oAuth2AuthorizationServerConfigurerProxy);
        http.csrf().ignoringRequestMatchers(
                //??????rest api??????
                new AntPathRequestMatcher(String.format("%s/**", authorizationServerPathOption.getRestApiContextPath()))
        );
        //???????????????api???????????????
        //?????????????????????api????????????
        http.authorizeRequests().antMatchers(String.format("%s/**", authorizationServerPathOption.getRestApiContextPath()), authorizationServerPathOption.getWebAuthenticationErrorPage(), authorizationServerPathOption.getWebMfaChallengePage()).permitAll();
        http.authorizeRequests().anyRequest().authenticated().and()
//todo ??????????????????????????????
//                .addFilterAfter(accessContextCheckFilter(), SecurityContextPersistenceFilter.class)
                .logout()
//                .addLogoutHandler(accessContextLogoutHandler)
                .logoutRequestMatcher(new AntPathRequestMatcher(authorizationServerPathOption.getWebLogoutEndpoint()))
                .logoutSuccessUrl(authorizationServerPathOption.getWebLogoutPage())
                .and().exceptionHandling().authenticationEntryPoint(
                        (request, response, authException) -> {
                            //fixed ??????????????????????????????
                            if (!oAuth2AuthorizationServerConfigurerProxy.getEndpointMatcher().matches(request) || oAuth2AuthorizationServerConfigurerProxy.getAuthorizationEndpointMatcher().matches(request)) {
                                response.sendRedirect(authorizationServerPathOption.getWebLoginPage());
                            } else {
                                authenticationEndpointAuthenticationFailureHandler.onAuthenticationFailure(
                                        request, response, new OAuth2AuthenticationException(
                                                new OAuth2Error(
                                                        OAuth2ErrorCodes.UNAUTHORIZED,
                                                        messageSource.getMessage(
                                                                authException,
                                                                //todo ??????????????????
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
        //??????oauth2???????????????
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
