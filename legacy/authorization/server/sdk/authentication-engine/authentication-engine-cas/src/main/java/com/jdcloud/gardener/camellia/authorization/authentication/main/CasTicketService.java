package com.jdcloud.gardener.camellia.authorization.authentication.main;

import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.CasExceptions;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;
import org.springframework.lang.Nullable;

/**
 * 将登录票据换成登录名使用
 *
 * @author zhanghan30
 * @date 2022/8/23 4:04 下午
 */
@FunctionalInterface
public interface CasTicketService<P extends BasicPrincipal> {
    /**
     * 使用票据换取登录名
     *
     * @param ticket 票据
     * @return 登录名 - 如果票据已经过期或不正确等，可以返回null
     * @throws CasExceptions.CasException 如果有问题抛出cas相关的异常
     * @see com.jdcloud.gardener.camellia.authorization.authentication.main.exception.client.InvalidTicketException
     * @see com.jdcloud.gardener.camellia.authorization.authentication.main.exception.client.InvalidUserException
     * @see com.jdcloud.gardener.camellia.authorization.authentication.main.exception.server.CasServerException
     */
    @Nullable
    P getPrincipal(String ticket) throws CasExceptions.CasException;
}
