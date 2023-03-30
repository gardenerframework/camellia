package io.gardenerframework.camellia.authentication.infra.challenge.mfa.server;

import io.gardenerframework.camellia.authentication.common.client.schema.RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.ChallengeResponseService;
import io.gardenerframework.camellia.authentication.infra.challenge.core.Scenario;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.Challenge;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeContext;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeRequest;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * @author zhanghan30
 * @date 2023/3/29 13:54
 */
public interface MfaAuthenticator<
        R extends ChallengeRequest,
        C extends Challenge,
        X extends ChallengeContext> extends ChallengeResponseService<R, C, X> {
    /**
     * 获取挑战请求
     *
     * @param userData 用户数据
     * @param client   客户端
     * @param scenario 场景
     * @return 挑战请求
     */
    R createChallengeRequest(
            @NonNull Map<String, Object> userData,
            @Nullable RequestingClient client,
            @NonNull Class<? extends Scenario> scenario
    );
}
