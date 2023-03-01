package com.jdcloud.gardener.camellia.authorization.authentication.main.event.schema;

import com.jdcloud.gardener.camellia.authorization.authentication.main.client.schema.Client;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;
import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 认证当前客户端的事件
 * <p>
 * 开发人员自行完成认证逻辑
 * <p>
 * 比如当前客户端是否有问题，需要停止一段时间
 * <p>
 * 客户端是否已经封了，不让用了
 *
 * @author zhanghan30
 * @date 2022/5/12 7:24 下午
 */
@Getter
public class AuthenticateClientEvent extends AbstractAuthenticationEvent {
    /**
     * 在系统中注册的客户端
     * <p>
     */
    private final RegisteredClient registeredClient;
    /**
     * http请求
     */
    private final HttpServletRequest request;

    public AuthenticateClientEvent(MultiValueMap<String, String> headers, String authenticationType, BasicPrincipal principal, String clientGroup, @Nullable Client client, Map<String, Object> context, RegisteredClient registeredClient, HttpServletRequest request) {
        super(headers, authenticationType, principal, clientGroup, client, context);
        this.registeredClient = registeredClient;
        this.request = request;
    }
}
