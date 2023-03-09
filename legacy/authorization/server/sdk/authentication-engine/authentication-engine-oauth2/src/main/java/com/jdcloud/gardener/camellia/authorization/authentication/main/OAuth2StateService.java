package com.jdcloud.gardener.camellia.authorization.authentication.main;

import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.client.InvalidStateException;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhanghan30
 * @date 2022/11/10 11:08
 */
public interface OAuth2StateService {
    /**
     * 执行state验证
     * <p>
     * 验证通过应当同时令state失效
     *
     * @param authenticationService 认证服务，不同的sns认证服务之间的state不能彼此覆盖
     * @param httpRequest           http请求
     * @param state                 输入的state
     * @throws InvalidStateException state不合法
     */
    void validate(
            OAuth2AuthenticationServiceBase authenticationService,
            HttpServletRequest httpRequest,
            String state
    ) throws InvalidStateException;

    /**
     * 创建一个state
     *
     * @param authenticationService 认证服务，不同的sns认证服务之间的state不能彼此覆盖
     * @param httpRequest           http请求
     * @return 生成的state
     */
    String createState(
            OAuth2AuthenticationServiceBase authenticationService,
            HttpServletRequest httpRequest
    );
}
