package com.jdcloud.gardener.camellia.authorization.test.security.authentication.principal;

import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;

/**
 * @author ZhangHan
 * @date 2022/5/13 11:54
 */
public class DisabledPrincipal extends BasicPrincipal {

    public DisabledPrincipal(String name) {
        super(name);
    }
}
