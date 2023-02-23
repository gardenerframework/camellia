package com.jdcloud.gardener.camellia.uac.client.schema.trait;

import com.jdcloud.gardener.fragrans.sugar.trait.annotation.Trait;

import java.util.Collection;

/**
 * @author zhanghan30
 * @date 2022/11/5 15:17
 */
@Trait
public class Scope {
    /**
     * oauth2的scope
     */
    private Collection<String> scope;
}
