package io.gardenerframework.camellia.authentication.server.main.event.schema;

import io.gardenerframework.camellia.authentication.server.main.UserAuthenticationService;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.AuthenticationException;

/**
 * 用户完成了加载
 * <p>
 * 这时下一步的逻辑就是{@link UserAuthenticationService}去认证了
 * <p>
 * 在加载完成之后，认证开始前，如果要加入检查逻辑，那开发人员自行添加
 * <p>
 * 如果有问题腰中断认证过程，则抛出{@link AuthenticationException}
 *
 * @author ZhangHan
 * @date 2022/5/11 9:34
 * @see UserAuthenticatedEvent
 */
@SuperBuilder
public class UserLoadedEvent extends AuthenticationEvent {
    /**
     * 加载完成的用户
     */
    @Getter
    @NonNull
    private final User user;
}
