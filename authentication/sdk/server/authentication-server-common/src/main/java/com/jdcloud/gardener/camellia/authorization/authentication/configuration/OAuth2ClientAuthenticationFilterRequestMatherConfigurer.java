package com.jdcloud.gardener.camellia.authorization.authentication.configuration;

import org.springframework.security.oauth2.server.authorization.web.OAuth2ClientAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;

/**
 * 用于配置{@link OAuth2ClientAuthenticationFilter}负责和不负责的路径
 * <p>
 * 这是因为部分rest api需要客户端信息，而上面的filter负责客户端认证但却不管除了oauth2之外的别的路径
 *
 * @author ZhangHan
 * @date 2022/4/26 22:09
 */
@FunctionalInterface
public interface OAuth2ClientAuthenticationFilterRequestMatherConfigurer {
    /**
     * 完成配置
     *
     * @param registry 注册表
     */
    void config(List<RequestMatcher> registry);
}
