package io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema;

import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeRequest;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Map;

/**
 * mfa认证过程中发生的挑战
 *
 * @author ZhangHan
 * @date 2022/5/15 21:12
 */
@Getter
@Setter
@AllArgsConstructor
public class AuthenticationServerMfaChallengeRequest implements ChallengeRequest {
    /**
     * 实际的认证器名称
     */
    @NonNull
    private String authenticatorName;
    /**
     * 发起挑战时，用户使用的登录名
     */
    @NonNull
    private Principal principal;
    /**
     * 完成基本认证的用户
     */
    @NonNull
    private User user;
    /**
     * 认证过程中使用的上下文
     */
    @NonNull
    private Map<String, Object> context;
}
