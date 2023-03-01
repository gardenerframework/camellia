package com.jdcloud.gardener.camellia.uac.application.schema.trait;

import com.jdcloud.gardener.fragrans.sugar.trait.annotation.Trait;

/**
 * @author zhanghan30
 * @date 2022/11/5 15:10
 */
@Trait
public interface Logo {
    /**
     * logo的url地址或者base64的数据
     */
    String logo = "";
}
