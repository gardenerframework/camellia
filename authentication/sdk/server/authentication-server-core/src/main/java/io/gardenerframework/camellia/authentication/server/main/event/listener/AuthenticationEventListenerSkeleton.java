package io.gardenerframework.camellia.authentication.server.main.event.listener;

import io.gardenerframework.camellia.authentication.server.main.event.schema.*;
import io.gardenerframework.camellia.authentication.server.main.exception.annotation.OAuth2ErrorCode;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 为了方便实现事件监听，写的一个监听框架接口
 * <p>
 * 具体实现时，监听什么重载什么，并加上{@link EventListener}注解自己
 *
 * @author ZhangHan
 * @date 2022/4/28 0:32
 */
public interface AuthenticationEventListenerSkeleton {
    /**
     * 当客户端要完成认证时
     *
     * @param event 认证事件
     * @throws AuthenticationException 认证过程需要中断
     *                                 这里的{@link AuthenticationException}会被引擎转为{@link OAuth2AuthenticationException}
     *                                 开发人员需要做的是标记好{@link ResponseStatus}和{@link OAuth2ErrorCode}
     */
    default void onAuthenticateClient(AuthenticateClientEvent event) throws AuthenticationException {

    }

    /**
     * 当用户要被加载前
     *
     * @param event 事件
     * @throws AuthenticationException 认证过程需要中断
     */
    default void onBeforeLoadingUser(BeforeLoadingUserEvent event) throws AuthenticationException {

    }

    /**
     * 当用户完成了加载
     *
     * @param event 事件
     * @throws AuthenticationException 认证过程需要中断
     */
    default void onUserLoaded(UserLoadedEvent event) throws AuthenticationException {

    }

    /**
     * 当用户完成了用户名密码等过程的认证
     *
     * @param event 事件
     * @throws AuthenticationException 认证过程需要中断
     */
    default void onUserAuthenticated(UserAuthenticatedEvent event) throws AuthenticationException {

    }

    /**
     * 认证遇到了问题发生了失败，其中要注意，捕捉到的异常可能是别的监听器抛出的，注意分辨哪些要处理哪些不要
     *
     * @param event 事件
     * @throws AuthenticationException 需要将认证错误转为其它问题抛出
     */
    default void onAuthenticationFailed(AuthenticationFailedEvent event) throws AuthenticationException {

    }


    /**
     * 认证成功
     * <p>
     * 不允许抛异常！！！
     *
     * @param event 事件
     */
    default void onAuthenticationSuccess(AuthenticationSuccessEvent event) {

    }
}
