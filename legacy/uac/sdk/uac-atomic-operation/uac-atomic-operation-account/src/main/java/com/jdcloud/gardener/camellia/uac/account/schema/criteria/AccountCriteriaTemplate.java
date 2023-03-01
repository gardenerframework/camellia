package com.jdcloud.gardener.camellia.uac.account.schema.criteria;

import com.jdcloud.gardener.fragrans.data.persistence.criteria.annotation.Batch;
import com.jdcloud.gardener.fragrans.data.trait.account.AccountTraits;
import com.jdcloud.gardener.fragrans.data.trait.contact.ContactTraits;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import com.jdcloud.gardener.fragrans.data.trait.sns.BioTraits;
import com.jdcloud.gardener.fragrans.data.trait.sns.SnsTraits;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Collection;

/**
 * 账户查询条件模板
 * <p>
 * 现场按照扩展查询条件进行扩展和实现
 *
 * @author zhanghan30
 * @date 2022/9/24 01:54
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class AccountCriteriaTemplate implements
        GenericTraits.Id<String>,
        GenericTraits.Ids<String>,
        AccountTraits.Username,
        SnsTraits.WeChatOpenId,
        SnsTraits.AlipayOpenId,
        SnsTraits.DingTalkOpenId,
        SnsTraits.EnterpriseWeChatOpenId,
        SnsTraits.LarkOpenId,
        BioTraits.FaceId,
        GenericTraits.Name,
        ContactTraits.MobilePhoneNumber,
        ContactTraits.Email {
    /**
     * 账户id
     */
    private String id;
    /**
     * 账户id清单
     * <p>
     * 批量搜索
     */
    @Batch(GenericTraits.Id.class)
    private Collection<String> ids;
    /**
     * 用户名
     * <p>
     * 判等
     */
    private String username;
    /**
     * 微信号
     * <p>
     * 判等
     */
    private String weChatOpenId;
    /**
     * 支付宝账号
     * <p>
     * 判等
     */
    private String alipayOpenId;
    /**
     * 企业微信id
     */
    private String enterpriseWeChatOpenId;
    /**
     * 钉钉id
     */
    private String dingTalkOpenId;
    /**
     * 飞书id
     */
    private String larkOpenId;
    /**
     * 人脸id
     */
    private String faceId;
    /**
     * 姓名 - 判等
     */
    private String name;
    /**
     * 手机号 - 判等
     */
    private String mobilePhoneNumber;
    /**
     * 邮箱 - 判等
     */
    private String email;
}
