package com.jdcloud.gardener.camellia.authorization.authentication.main.event.schema;

import com.jdcloud.gardener.camellia.authorization.authentication.main.client.schema.Client;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.UserService;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.MultiValueMap;

import java.util.Map;

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
public class AuthenticationFailedEvent extends AbstractAuthenticationEvent {
    /**
     * 独取出来的用户信息，如果问题是发生在{@link UserService}读取用户之前，则没有这个属性
     */
    @Nullable
    private final User user;
    /**
     * 捕捉到的认证异常
     */
    private final AuthenticationException exception;

    public AuthenticationFailedEvent(MultiValueMap<String, String> headers, String authenticationType, BasicPrincipal principal, String clientGroup, @Nullable Client client, Map<String, Object> context, @Nullable User user, AuthenticationException exception) {
        super(headers, authenticationType, principal, clientGroup, client, context);
        this.user = user;
        this.exception = exception;
    }
}
