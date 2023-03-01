package com.jdcloud.gardener.camellia.uac.account.defaults.atomic;

import com.jdcloud.gardener.camellia.uac.account.atomic.PrincipalQueryCriteriaFactory;
import com.jdcloud.gardener.camellia.uac.account.defaults.schema.criteria.DefaultAccountCriteria;
import com.jdcloud.gardener.camellia.uac.account.defaults.schema.entity.DefaultAccountEntity;
import com.jdcloud.gardener.fragrans.data.trait.account.AccountTraits;
import com.jdcloud.gardener.fragrans.data.trait.contact.ContactTraits;
import com.jdcloud.gardener.fragrans.data.trait.sns.BioTraits;
import com.jdcloud.gardener.fragrans.data.trait.sns.SnsTraits;

import java.util.Arrays;
import java.util.Collection;

/**
 * 当只有默认mapper启动的时候才会
 *
 * @author zhanghan30
 * @date 2022/11/4 15:22
 */
public class DefaultPrincipalQueryCriteriaFactory implements PrincipalQueryCriteriaFactory<DefaultAccountEntity, DefaultAccountCriteria> {
    @Override
    public DefaultAccountCriteria createQueryCriteriaByAccountByMobilePhoneNumber(String mobilePhoneNumber) {
        return DefaultAccountCriteria.builder().mobilePhoneNumber(mobilePhoneNumber).build();
    }

    @Override
    public DefaultAccountCriteria createQueryCriteriaByAccountByEmail(String email) {
        return DefaultAccountCriteria.builder().email(email).build();
    }

    @Override
    public DefaultAccountCriteria createQueryCriteriaByAccount(DefaultAccountEntity account) {
        DefaultAccountCriteria criteria = new DefaultAccountCriteria();
        criteria.setUsername(account.getUsername());
        criteria.setEmail(account.getEmail());
        criteria.setMobilePhoneNumber(account.getMobilePhoneNumber());
        criteria.setAlipayOpenId(account.getAlipayOpenId());
        criteria.setWeChatOpenId(account.getWeChatOpenId());
        criteria.setDingTalkOpenId(account.getDingTalkOpenId());
        criteria.setEnterpriseWeChatOpenId(account.getEnterpriseWeChatOpenId());
        criteria.setLarkOpenId(account.getLarkOpenId());
        criteria.setFaceId(account.getFaceId());
        return criteria;
    }

    @Override
    public Collection<Class<?>> getPrincipalFieldTraits() {
        return Arrays.asList(
                AccountTraits.Username.class,
                ContactTraits.Email.class,
                ContactTraits.MobilePhoneNumber.class,
                SnsTraits.AlipayOpenId.class,
                SnsTraits.WeChatOpenId.class,
                SnsTraits.DingTalkOpenId.class,
                SnsTraits.EnterpriseWeChatOpenId.class,
                SnsTraits.LarkOpenId.class,
                BioTraits.FaceId.class
        );
    }
}
