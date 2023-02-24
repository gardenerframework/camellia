package io.gardenerframework.camellia.authentication.server.main;

import io.gardenerframework.camellia.authentication.server.main.exception.client.BadAuthenticationRequestParameterException;
import io.gardenerframework.camellia.authentication.server.main.schema.UserAuthenticationRequestToken;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.NonNull;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ZhangHan
 * @date 2022/1/1 0:15
 */
public interface UserAuthenticationService {
    /**
     * 将http请求转为登录请求对象
     * <p>
     * 如果开发人员认为应当让登录页导向错误页面并定义错误的内容，抛出{@link AuthenticationException}
     * <p>
     * 其它类型的异常都将被解释为认证服务内部错误
     *
     * @param request http 请求
     * @return 登录请求对象
     * @throws AuthenticationException 如果认为当前认证过程应当中断，则抛出异常，
     *                                 比如{@link BadAuthenticationRequestParameterException}来表示参数有问题
     */
    UserAuthenticationRequestToken convert(@NonNull HttpServletRequest request) throws AuthenticationException;

    /**
     * 执行认证
     *
     * @param authenticationRequest 页面发来的认证请求
     * @param user                  用户详情
     * @throws AuthenticationException 有问题抛异常
     */
    void authenticate(@NonNull UserAuthenticationRequestToken authenticationRequest, @NonNull User user) throws AuthenticationException;
}
