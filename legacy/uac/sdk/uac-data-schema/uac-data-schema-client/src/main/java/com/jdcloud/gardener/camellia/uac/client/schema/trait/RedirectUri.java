package com.jdcloud.gardener.camellia.uac.client.schema.trait;

import com.jdcloud.gardener.fragrans.sugar.trait.annotation.Trait;

import java.util.Collection;

/**
 * @author zhanghan30
 * @date 2022/11/5 15:17
 */
@Trait
public class RedirectUri {
    /**
     * oauth2的回调地址
     */
    private Collection<String> redirectUri;
}
