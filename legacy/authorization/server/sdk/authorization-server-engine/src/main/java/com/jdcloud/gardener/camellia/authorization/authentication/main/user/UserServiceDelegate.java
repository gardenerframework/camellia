package com.jdcloud.gardener.camellia.authorization.authentication.main.user;

import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.credentials.PasswordCredentials;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
import com.jdcloud.gardener.camellia.authorization.common.annotation.AuthorizationEnginePreserved;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * @author ZhangHan
 * @date 2022/4/28 11:21
 */
@AuthorizationEnginePreserved
@Component
@Primary
public class UserServiceDelegate implements UserService {
    /**
     * 认证服务器内部试用的
     */
    private final Collection<UserService> preserved;
    /**
     * 代理的{@link UserService}
     */
    private UserService target;

    public UserServiceDelegate(Collection<UserService> services) {
        Assert.isTrue(!CollectionUtils.isEmpty(services), "no UserService loaded");
        this.preserved = new ArrayList<>(services.size() - 1);
        for (UserService service : services) {
            AuthorizationEnginePreserved annotation = AnnotationUtils.findAnnotation(service.getClass(), AuthorizationEnginePreserved.class);
            if (annotation == null) {
                Assert.isNull(target, "nonPreserved already existed");
                target = service;
            } else {
                preserved.add(service);
            }
        }
        Assert.notNull(target, "no UserService bean available");
    }

    /**
     * 先看看预留的能否认证
     *
     * @param principal   登录名
     * @param credentials 密码
     * @param context     上下文
     * @return 用户
     * @throws AuthenticationException 有问题
     */
    @Nullable
    @Override
    public User authenticate(BasicPrincipal principal, PasswordCredentials credentials, Map<String, Object> context) throws AuthenticationException {
        //先执行内部逻辑
        if (!CollectionUtils.isEmpty(preserved)) {
            for (UserService userService : preserved) {
                User user = userService.authenticate(principal, credentials, context);
                if (user != null) {
                    return user;
                }
            }
        }
        //再执行代理目标
        return target.authenticate(principal, credentials, context);
    }

    /**
     * 先看看预留的能否家在
     *
     * @param principal 登录名
     * @param context   上下文
     * @return 用户
     * @throws AuthenticationException       有问题
     * @throws UnsupportedOperationException 目标服务不支持读取
     */
    @Nullable
    @Override
    public User load(BasicPrincipal principal, Map<String, Object> context) throws AuthenticationException, UnsupportedOperationException {
        if (!CollectionUtils.isEmpty(preserved)) {
            for (UserService userService : preserved) {
                User user = userService.load(principal, context);
                if (user != null) {
                    return user;
                }
            }
        }
        //再执行代理目标
        return target.load(principal, context);
    }
}
