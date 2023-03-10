package io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema;

import io.gardenerframework.camellia.authentication.common.client.schema.OAuth2RequestingClient;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeContext;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import io.gardenerframework.fragrans.sugar.trait.annotation.Trait;

/**
 * @author zhanghan30
 * @date 2021/12/28 10:58 上午
 */
@Trait
public interface MfaAuthenticationChallengeContext extends ChallengeContext,
        MfaAuthenticationChallengeContextAutoSavingContract {
    /**
     * 触发当前mfa认证的登录名
     * <p>
     * 用于重放认证成功事件
     */
    Principal principal = null;
    /**
     * 当时正在请求的客户端
     */
    OAuth2RequestingClient client = null;
    /**
     * 当时已经通过基本校验的用户
     */
    User user = null;
}
