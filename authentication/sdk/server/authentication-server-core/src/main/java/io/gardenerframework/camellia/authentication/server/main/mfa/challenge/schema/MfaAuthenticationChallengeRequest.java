package io.gardenerframework.camellia.authentication.server.main.mfa.challenge.schema;

import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeRequest;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import io.gardenerframework.fragrans.sugar.trait.annotation.Trait;

import java.util.Map;

/**
 * mfa认证过程中发生的挑战
 *
 * @author ZhangHan
 * @date 2022/5/15 21:12
 */
@Trait
public interface MfaAuthenticationChallengeRequest extends ChallengeRequest,
        MfaAuthenticationChallengeContextAutoSavingContract {
    /**
     * 发起挑战时，用户使用的登录名
     */
    Principal principal = null;
    /**
     * 完成基本认证的用户
     */
    User user = null;
    /**
     * 认证过程中使用的上下文
     */
    Map<String, Object> context = null;
}
