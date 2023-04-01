package io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema;

import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class AuthenticationServerMfaAuthenticationChallenge extends Challenge {
    /**
     * 内部实际发送的挑战
     * <p>
     * 防止挑战应答的逻辑干扰内部生成出来的结果
     */
    @NonNull
    private Challenge target;
}
