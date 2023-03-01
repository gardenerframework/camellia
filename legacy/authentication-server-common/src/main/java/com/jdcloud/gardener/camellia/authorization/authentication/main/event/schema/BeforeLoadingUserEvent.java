package com.jdcloud.gardener.camellia.authorization.authentication.main.event.schema;

import com.jdcloud.gardener.camellia.authorization.authentication.main.client.schema.Client;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.UserService;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.MultiValueMap;

import java.util.Map;

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
public class BeforeLoadingUserEvent extends AbstractAuthenticationEvent {
    public BeforeLoadingUserEvent(MultiValueMap<String, String> headers, String authenticationType, BasicPrincipal principal, String clientGroup, @Nullable Client client, Map<String, Object> context) {
        super(headers, authenticationType, principal, clientGroup, client, context);
    }
}
