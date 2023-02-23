package io.gardenerframework.camellia.authentication.server.main.schema;

import io.gardenerframework.camellia.authentication.server.main.UserAuthenticationService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;

/**
 * 登录请求的上下文
 *
 * @author zhanghan30
 * @date 2022/5/12 2:08 下午
 * @see LoginAuthenticationRequestToken
 */
@Getter
@Setter
@AllArgsConstructor
public class LoginAuthenticationRequestContext {
    /**
     * 由哪个服务来完成认证过程
     */
    @NonNull
    private final UserAuthenticationService userAuthenticationService;
    /**
     * 携带的http请求
     */
    @NonNull
    private final HttpServletRequest httpServletRequest;
}
