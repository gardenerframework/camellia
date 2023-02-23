package io.gardenerframework.camellia.authentication.server.utils;

import io.gardenerframework.camellia.authentication.server.configuration.AuthenticationServerEngineSecurityConfigurer;
import io.gardenerframework.camellia.authentication.server.configuration.WebAuthenticationEndpointFilterConfigurer;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2EndpointConfigurerRequestMatcherAccessor;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2TokenEndpointConfigurer;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 这个工具类主要就是判断当前是什么路径
 * <p>
 * 它会服从oauth2和网页端的request matcher
 *
 * @author ZhangHan
 * @date 2022/5/11 23:32
 */
@Component
public class AuthenticationEndpointMatcher extends AuthenticationServerEngineSecurityConfigurer {
    private RequestMatcher webAuthenticationEndpointMatcher;
    private RequestMatcher tokenAuthenticationEndpointMatcher;


    @Override
    public void configure(HttpSecurity builder) throws Exception {
        config(
                builder.getConfigurer(OAuth2AuthorizationServerConfigurer.class),
                builder.getConfigurer(WebAuthenticationEndpointFilterConfigurer.class)
        );
    }

    private void config(
            OAuth2AuthorizationServerConfigurer oAuth2AuthorizationServerConfigurer,
            WebAuthenticationEndpointFilterConfigurer webAuthenticationEndpointFilterConfigurer
    ) {
        oAuth2AuthorizationServerConfigurer.tokenEndpoint(
                (Customizer<OAuth2TokenEndpointConfigurer>) oAuth2TokenEndpointConfigurer ->
                        tokenAuthenticationEndpointMatcher = OAuth2EndpointConfigurerRequestMatcherAccessor.getTokenEndpointRequestMatcher(oAuth2TokenEndpointConfigurer)
        );
        this.webAuthenticationEndpointMatcher = webAuthenticationEndpointFilterConfigurer.getEndpointMatcher();
    }

    /**
     * 判断是否是token 认证端点
     *
     * @param request http 请求
     * @return 判断结果
     */
    public boolean isTokenEndpoint(HttpServletRequest request) {
        return tokenAuthenticationEndpointMatcher.matches(request);
    }

    /**
     * 判断是否是网页认证端点
     *
     * @param request http 请求
     * @return 判断结果
     */
    public boolean isWebAuthenticationEndpoint(HttpServletRequest request) {
        return webAuthenticationEndpointMatcher.matches(request);
    }
}
