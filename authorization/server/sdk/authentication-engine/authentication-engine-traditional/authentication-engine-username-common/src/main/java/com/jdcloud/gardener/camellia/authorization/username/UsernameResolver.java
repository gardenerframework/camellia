package com.jdcloud.gardener.camellia.authorization.username;

import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;
import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;

/**
 * @author ZhangHan
 * @date 2022/5/13 20:22
 */
@FunctionalInterface
public interface UsernameResolver {
    /**
     * 执行登录名转换
     *
     * @param username      输入的用户名
     * @param principalType 登录名类型
     * @return 转换好的
     * @throws AuthenticationException 如果觉得无法执行转换，抛出异常中断认证过程
     */
    BasicPrincipal resolve(String username, @Nullable String principalType) throws AuthenticationException;
}
