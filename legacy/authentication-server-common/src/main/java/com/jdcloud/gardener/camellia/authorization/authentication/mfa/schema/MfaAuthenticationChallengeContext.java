package com.jdcloud.gardener.camellia.authorization.authentication.mfa.schema;

import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.ChallengeContext;
import com.jdcloud.gardener.camellia.authorization.challenge.schema.ChallengeEnvironment;
import com.jdcloud.gardener.camellia.authorization.common.Version;
import com.jdcloud.gardener.fragrans.log.annotation.LogTarget;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author zhanghan30
 * @date 2021/12/28 10:58 上午
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@LogTarget("mfa认证请求")
public class MfaAuthenticationChallengeContext extends ChallengeContext {
    private static final long serialVersionUID = Version.current;
    /**
     * 上下文中要保存的，触发了
     */
    private BasicPrincipal principal;

    public MfaAuthenticationChallengeContext(ChallengeEnvironment request, Date expiresAt, boolean verified, BasicPrincipal principal) {
        super(request, expiresAt, verified);
        this.principal = principal;
    }
}
