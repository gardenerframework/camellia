package io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema;

import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * 用于mfa独立服务器客户端请求时的挑战请求输入参数
 * <p>
 * 其它的引擎都能搞定，用户信息和额外参数监听器负责搞定
 */
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
public class MfaAuthenticationServerClientChallengeRequest implements ChallengeRequest {
    /**
     * 请求的用户
     */
    @NonNull
    private Map<String, Object> user;
    /**
     * 请求的参数
     */
    @Nullable
    private Map<String, Object> additionalArguments;
}
