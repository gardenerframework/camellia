package io.gardenerframework.camellia.authentication.server.main.event.listener;

import io.gardenerframework.camellia.authentication.server.main.event.schema.UserAuthenticatedEvent;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;

import java.util.Date;

/**
 * @author ZhangHan
 * @date 2022/4/28 12:30
 */
public class UserStatusValidationListener implements AuthenticationEventListenerSkeleton {

    @Override
    @EventListener
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public void onUserAuthenticated(UserAuthenticatedEvent event) throws AuthenticationException {
        User user = event.getUser();
        if (!user.isEnabled()) {
            throw new DisabledException(user.getId());
        }
        if (user.isLocked()) {
            throw new LockedException(user.getId());
        }
        if (user.getSubjectExpiryDate() != null && new Date().after(user.getSubjectExpiryDate())) {
            throw new AccountExpiredException(user.getId());
        }
        //todo 密码过期怎么处理没想好
    }
}
