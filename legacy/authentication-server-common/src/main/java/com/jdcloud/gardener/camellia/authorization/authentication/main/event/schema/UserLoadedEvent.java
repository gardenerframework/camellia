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
public class UserLoadedEvent extends AbstractAuthenticationEvent {
    /**
     * 加载完成的用户
     */
    @Getter
    private final User user;

    public UserLoadedEvent(MultiValueMap<String, String> headers, String authenticationType, BasicPrincipal principal, String clientGroup, @Nullable Client client, Map<String, Object> context, User user) {
        super(headers, authenticationType, principal, clientGroup, client, context);
        this.user = user;
    }
}
