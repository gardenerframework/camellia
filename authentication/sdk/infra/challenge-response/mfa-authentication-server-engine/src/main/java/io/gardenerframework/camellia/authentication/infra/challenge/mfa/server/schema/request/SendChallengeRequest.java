package io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.request;

import java.util.Map;

/**
 * @author zhanghan30
 * @date 2023/3/28 15:26
 */
public class SendChallengeRequest {
    /**
     * 指定要使用的认证器
     */
    private String authenticator;
    /**
     * 要执行mfa的用户信息，按照实现方的理解来转类型
     * <p>
     * 最终这个认证器要能识别这个用户信息
     */
    private Map<String, Object> user;
    /**
     * 实际请求的客户端
     * <p>
     * 最终这个认证器要能识别这个客户端
     */
    private Map<String, Object> requestingClient;
}
