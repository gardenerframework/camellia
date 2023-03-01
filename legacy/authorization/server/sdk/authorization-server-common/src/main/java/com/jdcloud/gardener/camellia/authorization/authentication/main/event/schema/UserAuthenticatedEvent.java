package com.jdcloud.gardener.camellia.authorization.authentication.main.event.schema;

import com.jdcloud.gardener.camellia.authorization.authentication.main.UserAuthenticationService;
import com.jdcloud.gardener.camellia.authorization.authentication.main.client.schema.Client;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.MultiValueMap;

import java.util.Map;

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
public class UserAuthenticatedEvent extends AbstractAuthenticationEvent {
    /**
     * 加载出来的用户信息
     */
    private final User user;

    public UserAuthenticatedEvent(MultiValueMap<String, String> headers, String authenticationType, BasicPrincipal principal, String clientGroup, @Nullable Client client, Map<String, Object> context, User user) {
        super(headers, authenticationType, principal, clientGroup, client, context);
        this.user = user;
    }
}
