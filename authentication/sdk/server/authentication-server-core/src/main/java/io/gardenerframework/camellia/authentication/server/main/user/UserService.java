package io.gardenerframework.camellia.authentication.server.main.user;

import io.gardenerframework.camellia.authentication.server.main.schema.subject.credentials.PasswordCredentials;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

import java.util.Map;

/**
 * 用户加载服务
 *
 * @author ZhangHan
 * @date 2022/1/1 1:12
 */
public interface UserService {

    /**
     * 向统一用户数据访问接口发起认证请求
     * <p>
     * 如果密码有错则抛出{@link BadCredentialsException}
     *
     * @param principal   登录凭据名
     * @param credentials 密码
     * @param context     认证上下文中的共享属性，
     *                    新加了{@link Nullable}注解，如果发现上下文是null，说明当前用户服务正用于其它用途。
     *                    比如用于密码找回场景，或者其他非认证场景
     * @return 认证完毕的用户信息，如果不存在则返回{@code null}
     * @throws AuthenticationException 认证有问题
     */
    @Nullable
    User authenticate(Principal principal, PasswordCredentials credentials, @Nullable Map<String, Object> context) throws AuthenticationException;

    /**
     * 基于请求凭据去读取而不是认证用户，其余错误按需转为{@link AuthenticationException}
     * <p>
     *
     * @param principal 登录凭据
     * @param context   认证上下文中的共享属性，
     *                  新加了{@link Nullable}注解，如果发现上下文是null，说明当前用户服务正用于其它用途。
     *                  比如用于密码找回场景，或者其他非认证场景
     * @return 用户信息，如果不存在则返回{@code null}
     * @throws AuthenticationException       认证有问题
     * @throws UnsupportedOperationException 如果当前服务对接的用户存储根本不支持直接通过登录名查找用户
     */
    @Nullable
    User load(Principal principal, @Nullable Map<String, Object> context) throws AuthenticationException, UnsupportedOperationException;
}
