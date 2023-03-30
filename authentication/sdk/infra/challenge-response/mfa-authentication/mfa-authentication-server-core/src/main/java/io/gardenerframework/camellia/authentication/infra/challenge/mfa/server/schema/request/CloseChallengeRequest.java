package io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.request;

import io.gardenerframework.camellia.authentication.infra.challenge.mfa.server.schema.request.constraints.RequestingClientSupported;
import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2023/3/29 13:34
 */
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class CloseChallengeRequest {
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
    @NotBlank
    @NonNull
    private String scenario;
    /**
     * 挑战id
     */
    @NotBlank
    private String challengeId;
}
