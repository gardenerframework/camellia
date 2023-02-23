package com.jdcloud.gardener.camellia.uac.account.schema.trait;

import com.jdcloud.gardener.fragrans.data.trait.sns.SnsTraits;

/**
 * @author zhanghan30
 * @date 2022/11/9 12:24
 */
public interface SnsIds extends
        SnsTraits.AlipayOpenId,
        SnsTraits.DingTalkOpenId,
        SnsTraits.EnterpriseWeChatOpenId,
        SnsTraits.LarkOpenId,
        SnsTraits.WeChatOpenId {
}
