package com.jdcloud.gardener.camellia.authorization.authentication.main.event.schema;

import com.jdcloud.gardener.camellia.authorization.authentication.main.client.schema.Client;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;
import com.jdcloud.gardener.camellia.authorization.authentication.main.user.schema.subject.User;
import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

import java.util.Map;

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
public class AuthenticationSuccessEvent extends AbstractAuthenticationEvent {
    /**
     * 完成认证的用户信息
     */
    private final User user;

    public AuthenticationSuccessEvent(MultiValueMap<String, String> headers, String authenticationType, BasicPrincipal principal, String clientGroup, @Nullable Client client, Map<String, Object> context, User user) {
        super(headers, authenticationType, principal, clientGroup, client, context);
        this.user = user;
    }
}
