package io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.request;

import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.request.constraints.RequestingClientSupported;
import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2023/3/28 15:26
 */
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class SendChallengeRequest {
    /**
     * 要执行mfa的用户信息，按照实现方的理解来转类型
     * <p>
     * 最终这个认证器要能识别这个用户信息
     */
    @NonNull
    @NotNull
    private Map<String, Object> user;
    /**
     * 实际请求的客户端
     * <p>
     * 最终这个认证器要能识别这个客户端
     */
    @Nullable
    @RequestingClientSupported
    private Map<String, Object> requestingClient;
    /**
     * 执行mfa验证的场景，比如登录，比如下订单
     */
    @Nullable
    private String scenario;
}
