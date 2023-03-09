package com.jdcloud.gardener.camellia.authorization.authentication.main.event.listener;

import com.jdcloud.gardener.camellia.authorization.authentication.main.event.schema.UserAuthenticatedEvent;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author ZhangHan
 * @date 2022/4/28 12:30
 */
@Component
public class CommonAuthenticationListener implements AuthenticationEventListenerSkeleton {

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
