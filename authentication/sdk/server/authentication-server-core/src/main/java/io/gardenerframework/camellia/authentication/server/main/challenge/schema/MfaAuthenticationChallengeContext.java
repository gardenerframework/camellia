package io.gardenerframework.camellia.authentication.server.main.challenge.schema;

import io.gardenerframework.camellia.authentication.infra.challenge.core.Version;
import io.gardenerframework.camellia.authentication.infra.challenge.core.schema.ChallengeContext;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author zhanghan30
 * @date 2021/12/28 10:58 上午
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class MfaAuthenticationChallengeContext implements ChallengeContext {
    private static final long serialVersionUID = Version.current;
    /**
     * 触发当前mfa认证的登录名
     */
    private Principal principal;
}
