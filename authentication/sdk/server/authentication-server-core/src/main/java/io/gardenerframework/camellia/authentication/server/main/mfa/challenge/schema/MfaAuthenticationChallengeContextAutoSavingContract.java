package io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema;

import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import io.gardenerframework.fragrans.sugar.trait.annotation.Trait;

/**
 * @author zhanghan30
 * @date 2023/2/27 15:38
 */
@Trait
public interface MfaAuthenticationChallengeContextAutoSavingContract {
    /**
     * 发起挑战时，用户使用的登录名
     */
    Principal principal = null;
    /**
     * 完成基本认证的用户
     */
    User user = null;
}
