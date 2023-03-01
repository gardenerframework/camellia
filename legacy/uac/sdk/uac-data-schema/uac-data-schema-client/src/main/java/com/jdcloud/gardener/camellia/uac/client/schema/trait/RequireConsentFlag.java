package com.jdcloud.gardener.camellia.uac.client.schema.trait;

import com.jdcloud.gardener.fragrans.sugar.trait.annotation.Trait;

/**
 * @author zhanghan30
 * @date 2022/11/5 15:17
 */
@Trait
public class RequireConsentFlag {
    /**
     * oauth2的是否要求用户批准
     */
    private boolean requireConsent;
}
