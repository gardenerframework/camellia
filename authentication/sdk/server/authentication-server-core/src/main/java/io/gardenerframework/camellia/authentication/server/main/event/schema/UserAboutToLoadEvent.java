package io.gardenerframework.camellia.authentication.server.main.event.schema;

import io.gardenerframework.camellia.authentication.server.main.user.UserService;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.AuthenticationException;

/**
 * <p>
 * 发生于{@link UserService}加载用户前
 * <p>
 * 如果事件监听者要中断登录过程，则在事件监听程序中抛出{@link AuthenticationException}
 *
 * @author ZhangHan
 * @date 2022/4/27 22:26
 * @see UserLoadedEvent
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class UserAboutToLoadEvent extends AuthenticationEvent {
}
