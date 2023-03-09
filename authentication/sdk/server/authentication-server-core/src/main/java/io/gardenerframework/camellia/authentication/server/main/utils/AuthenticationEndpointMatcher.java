package io.gardenerframework.camellia.authentication.server.main.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhanghan30
 * @date 2023/3/6 13:03
 */
public interface AuthenticationEndpointMatcher {
    /**
     * 判断是否是token认证端点
     *
     * @param request http 请求
     * @return 判断结果
     */
    boolean isTokenEndpoint(HttpServletRequest request);

    /**
     * 判断是否是网页认证端点
     *
     * @param request http 请求
     * @return 判断结果
     */
    boolean isWebAuthenticationEndpoint(HttpServletRequest request);
}
