package com.jdcloud.gardener.camellia.authorization.authentication.main.event.schema;

import com.jdcloud.gardener.camellia.authorization.authentication.main.client.schema.Client;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

import java.util.Map;

/**
 * @author ZhangHan
 * @date 2022/4/27 23:42
 */
@Getter
@AllArgsConstructor
public abstract class AbstractAuthenticationEvent {
    /**
     * http请求头
     * <p>
     * 其中Authorization头已经被去掉，因为其中包含了access token或认证信息
     * <p>
     * http头用于给实现类一些基本的请求判断逻辑，特别是检查UserAgent判断是不是手机端，以及来源ip等
     */
    private final MultiValueMap<String, String> headers;
    /**
     * 认证方式
     */
    private final String authenticationType;
    /**
     * 登录请求的用户名以及类型
     */
    private final BasicPrincipal principal;
    /**
     * 应用组
     */
    private final String clientGroup;
    /**
     * 但前准备要访问系统的客户端
     * <p>
     * 不是token endpoint 没有客户端
     */
    @Nullable
    private final Client client;
    /**
     * 贯穿登录认证过程的上下文
     * <p>
     * 可以用来存取一些属性
     */
    private final Map<String, Object> context;

}
