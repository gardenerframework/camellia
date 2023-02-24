package io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema;

import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeRequest;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * mfa认证过程中发生的挑战
 *
 * @author ZhangHan
 * @date 2022/5/15 21:12
 */
@Getter
@SuperBuilder
public class MfaAuthenticationChallengeRequest implements ChallengeRequest {
    /**
     * 发起挑战时，用户使用的登录名
     */
    @NonNull
    private final Principal principal;
    /**
     * 认证过程中使用的上下文
     */
    @NonNull
    private final Map<String, Object> context;
}
