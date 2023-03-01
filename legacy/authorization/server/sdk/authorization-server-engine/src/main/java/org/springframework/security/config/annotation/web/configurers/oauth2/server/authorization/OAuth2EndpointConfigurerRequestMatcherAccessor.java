package org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization;

import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * @author ZhangHan
 * @date 2022/5/11 23:41
 */
public abstract class OAuth2EndpointConfigurerRequestMatcherAccessor {
    private OAuth2EndpointConfigurerRequestMatcherAccessor() {

    }

    /**
     * 我不太能理解为什么spring要把这个方法做成package-private
     *
     * @param oAuth2TokenEndpointConfigurer 配置器
     * @return 配置器里的RequestMatcher
     */
    public static RequestMatcher getTokenEndpointRequestMatcher(OAuth2TokenEndpointConfigurer oAuth2TokenEndpointConfigurer) {
        return oAuth2TokenEndpointConfigurer.getRequestMatcher();
    }

    /**
     * 返回授权接口的路径
     *
     * @param oAuth2AuthorizationEndpointConfigurer 配置类
     * @return 配置器里的RequestMatcher
     */
    public static RequestMatcher getAuthorizationEndpointRequestMatcher(OAuth2AuthorizationEndpointConfigurer oAuth2AuthorizationEndpointConfigurer) {
        return oAuth2AuthorizationEndpointConfigurer.getRequestMatcher();
    }
}
