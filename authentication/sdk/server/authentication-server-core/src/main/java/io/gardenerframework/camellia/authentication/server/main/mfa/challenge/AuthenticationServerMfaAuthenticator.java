package io.gardenerframework.camellia.authentication.server.main.mfa.challenge;

import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeResponseService;
import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeContext;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeRequest;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * 附加在{@link  ChallengeResponseService}上的接口，表达这个认证器是支持认证服务器的mfa场景
 *
 * 补充了要求这个认证器是个挑战应答服务
 */
public interface AuthenticationServerMfaAuthenticator<R extends ChallengeRequest, C extends Challenge, X extends ChallengeContext> extends ChallengeResponseService<R, C, X> {
    /**
     * 执行mfa认证过程的上下文到认证请求的转换
     *
     * @param client    当亲正在进行mfa登录的客户端
     * @param scenario  场景，固定为{@link AuthenticationServerMfaAuthenticationScenario}
     * @param principal 当前正在登录的用户名
     * @param user      已经完成读取的用户
     * @param context   登录过程中发生的上下文
     * @return 认证请求
     * @throws Exception 发生了问题
     */
    R authenticationContextToChallengeRequest(
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull Principal principal,
            @NonNull User user,
            @NonNull Map<String, Object> context
    ) throws Exception;
}
