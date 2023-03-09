package io.gardenerframework.camellia.authentication.server.main.event.schema;

import io.gardenerframework.camellia.authentication.server.main.UserAuthenticationService;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.AuthenticationException;

/**
 * 在{@link UserAuthenticationService}认证之后
 * <p>
 * 意味着用户成功从库中读出来，并且相关的登录凭据，比如短信验证码，也验证通过，即将生成登录态，通知用户登录成功
 * <p>
 * 开发人员在此加入自行的检查逻辑，如果认为应当中盾登录过程，则抛出{@link AuthenticationException}
 *
 * @author ZhangHan
 * @date 2022/4/28 12:11
 */
@Getter
@SuperBuilder
public class UserAuthenticatedEvent extends AuthenticationEvent {
    /**
     * 加载出来的用户信息
     */
    @NonNull
    private final User user;
}
