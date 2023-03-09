package com.jdcloud.gardener.camellia.authorization.authentication.main.schema;

import com.jdcloud.gardener.camellia.authorization.authentication.main.UserAuthenticationService;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.servlet.http.HttpServletRequest;

/**
 * 登录请求的上下文
 *
 * @author zhanghan30
 * @date 2022/5/12 2:08 下午
 * @see LoginAuthenticationRequestToken
 */
@Data
@AllArgsConstructor
public class LoginAuthenticationRequestContext {
    /**
     * 由哪个服务来完成认证过程
     */
    private final UserAuthenticationService userAuthenticationService;
    /**
     * 携带的http请求
     */
    private final HttpServletRequest httpServletRequest;
}
