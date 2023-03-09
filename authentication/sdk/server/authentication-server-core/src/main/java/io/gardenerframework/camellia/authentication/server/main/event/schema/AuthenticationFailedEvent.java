package io.gardenerframework.camellia.authentication.server.main.event.schema;

import io.gardenerframework.camellia.authentication.server.main.user.UserService;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;

/**
 * 在认证过程中捕捉到了异常
 * <p>
 * 如果认为要抛出别的异常，则监听器自己抛
 * <p>
 * 比如捕捉到错误密码异常，错误次数超过5次，要封账号，则监听器自己抛异常
 * <p>
 * 如果认为不需要改动之前的异常，则监听器不用抛异常，主逻辑会抛
 *
 * @author ZhangHan
 * @date 2022/4/28 13:46
 */
@Getter
@SuperBuilder
public class AuthenticationFailedEvent extends AuthenticationEvent {
    /**
     * 独取出来的用户信息，如果问题是发生在{@link UserService}读取用户之前，则没有这个属性
     */
    @Nullable
    private final User user;
    /**
     * 捕捉到的认证异常
     */
    @NonNull
    private final AuthenticationException exception;
}
