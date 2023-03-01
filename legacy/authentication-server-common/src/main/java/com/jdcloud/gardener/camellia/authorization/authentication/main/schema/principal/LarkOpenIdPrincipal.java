package com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal;

import com.jdcloud.gardener.camellia.authorization.common.Version;
import com.jdcloud.gardener.fragrans.log.annotation.ReferLogTarget;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @author ZhangHan
 * @date 2022/1/2 23:23
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ReferLogTarget(value = BasicPrincipal.class, prefix = "飞书openid类型的")
public class LarkOpenIdPrincipal extends BasicPrincipal {
    private static final long serialVersionUID = Version.current;

    public LarkOpenIdPrincipal(String name) {
        super(name);
    }
}
