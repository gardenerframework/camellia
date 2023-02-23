package com.jdcloud.gardener.camellia.uac.application.schema.trait;

import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;

/**
 * @author zhanghan30
 * @date 2022/11/5 15:18
 */
public interface BasicApplicationInformation extends
        HomepageUrl,
        GenericTraits.Name,
        GenericTraits.Description,
        Logo {
}
