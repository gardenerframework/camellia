package io.gardenerframework.camellia.authentication.server.main.event.schema;

import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

/**
 * 整个认证过程已经成功，意味着什么用户名密码检查，账户状态检查，mfa检查等都通过了，用户已经完成了登录过程
 * <p>
 * 监听器可以写写log，发发短信等杂项功能
 * <p>
 * 这个事件处理过程的全部异常会被omit
 *
 * @author ZhangHan
 * @date 2022/4/28 13:57
 */
@Getter
@SuperBuilder
public class AuthenticationSuccessEvent extends AuthenticationEvent {
    /**
     * 完成认证的用户信息
     */
    @NonNull
    private final User user;
}
