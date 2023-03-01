package com.jdcloud.gardener.camellia.uac.test.accout.extend;

import com.jdcloud.gardener.camellia.uac.account.atomic.PrincipalQueryCriteriaFactory;
import com.jdcloud.gardener.fragrans.data.trait.account.AccountTraits;
import com.jdcloud.gardener.fragrans.data.trait.contact.ContactTraits;
import com.jdcloud.gardener.fragrans.data.trait.sns.SnsTraits;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author zhanghan30
 * @date 2022/11/4 17:15
 */
@Component
@Primary
public class ExtendPrincipalQueryCriteriaFactory implements PrincipalQueryCriteriaFactory<ExtendAccount, ExtendCriteria> {
    @Override
    public ExtendCriteria createQueryCriteriaByAccountByMobilePhoneNumber(String mobilePhoneNumber) {
        return ExtendCriteria.builder().mobilePhoneNumber(mobilePhoneNumber).build();
    }

    @Override
    public ExtendCriteria createQueryCriteriaByAccountByEmail(String email) {
        return ExtendCriteria.builder().email(email).build();
    }

    @Override
    public ExtendCriteria createQueryCriteriaByAccount(ExtendAccount account) {
        ExtendCriteria criteria = new ExtendCriteria();
        criteria.setUsername(account.getUsername());
        criteria.setEmail(account.getEmail());
        criteria.setMobilePhoneNumber(account.getMobilePhoneNumber());
        criteria.setAlipayOpenId(account.getAlipayOpenId());
        criteria.setWeChatOpenId(account.getWeChatOpenId());
        return criteria;
    }

    @Override
    public Collection<Class<?>> getPrincipalFieldTraits() {
        return Arrays.asList(
                AccountTraits.Username.class,
                ContactTraits.Email.class,
                ContactTraits.MobilePhoneNumber.class,
                SnsTraits.AlipayOpenId.class,
                SnsTraits.WeChatOpenId.class
        );
    }
}
