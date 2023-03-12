package io.gardenerframework.camellia.authentication.server.main.mfa.challenge;


import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeResponseService;
import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.MfaAuthenticationChallengeContext;
import io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema.MfaAuthenticationChallengeRequest;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * @author zhanghan30
 * @date 2021/12/28 10:57 上午
 */
public interface MfaAuthenticationChallengeResponseService<
        R extends MfaAuthenticationChallengeRequest,
        X extends MfaAuthenticationChallengeContext> extends ChallengeResponseService<R, Challenge, X> {
    /**
     * 引擎实际调用的方法
     *
     * @param client    客户端
     * @param scenario  场景
     * @param principal 当前登录名
     * @param user      读取出的用户
     * @param context   认证过程中的上下文
     * @return 挑战请求
     * @throws Exception 发生错误
     */
    Challenge sendChallenge(
            @Nullable OAuth2RequestingClient client,
            @NonNull Class<? extends Scenario> scenario,
            @NonNull Principal principal,
            @NonNull User user,
            @NonNull Map<String, Object> context
    ) throws Exception;
}
