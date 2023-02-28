package io.gardenerframework.camellia.authentication.server.main.configuration;

import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;

/**
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
