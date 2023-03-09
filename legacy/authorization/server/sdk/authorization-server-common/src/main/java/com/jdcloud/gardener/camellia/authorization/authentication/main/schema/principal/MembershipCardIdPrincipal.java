package com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal;

import com.jdcloud.gardener.camellia.authorization.common.Version;
import com.jdcloud.gardener.fragrans.log.annotation.ReferLogTarget;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @author zhanghan30
 * @date 2022/4/25 5:04 下午
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ReferLogTarget(value = BasicPrincipal.class, prefix = "会员卡号类型的")
public class MembershipCardIdPrincipal extends BasicPrincipal {
    private static final long serialVersionUID = Version.current;

    public MembershipCardIdPrincipal(String name) {
        super(name);
    }
}
