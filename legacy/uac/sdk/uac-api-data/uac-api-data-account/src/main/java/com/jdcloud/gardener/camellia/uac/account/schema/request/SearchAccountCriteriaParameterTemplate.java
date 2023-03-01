package com.jdcloud.gardener.camellia.uac.account.schema.request;

import com.jdcloud.gardener.camellia.uac.common.schema.request.SearchCriteriaParameterBase;
import com.jdcloud.gardener.fragrans.api.standard.schema.trait.ApiStandardDataTraits;
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
 * 基础的用户搜索条件
 * <p>
 * 在此添加的搜索账户表属性的条件
 *
 * @author zhanghan30
 * @date 2022/8/12 10:12 上午
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class SearchAccountCriteriaParameterTemplate extends SearchCriteriaParameterBase
        implements
        ApiStandardDataTraits.Id<String>,
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
    private String id;
    /**
     * 用户名
     * <p>
     * 用于非密码登录的搜索场景
     */
    private String username;
    /**
     * 微信号
     * <p>
     * 用于非密码登录的搜索场景
     */
    private String weChatOpenId;
    /**
     * 支付宝
     * <p>
     * 用于非密码登录的搜索场景
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
     * 搜索的姓名
     */
    private String name;
    /**
     * 要搜索的邮箱
     */
    private String email;
    /**
     * 手机号
     */
    private String mobilePhoneNumber;

    public SearchAccountCriteriaParameterTemplate(Collection<String> must, Collection<String> should, String id, String username, String weChatOpenId, String alipayOpenId, String enterpriseWeChatOpenId, String dingTalkOpenId, String larkOpenId, String faceId, String name, String email, String mobilePhoneNumber) {
        super(must, should);
        this.id = id;
        this.username = username;
        this.weChatOpenId = weChatOpenId;
        this.alipayOpenId = alipayOpenId;
        this.enterpriseWeChatOpenId = enterpriseWeChatOpenId;
        this.dingTalkOpenId = dingTalkOpenId;
        this.larkOpenId = larkOpenId;
        this.faceId = faceId;
        this.name = name;
        this.email = email;
        this.mobilePhoneNumber = mobilePhoneNumber;
    }
}
