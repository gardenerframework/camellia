package com.jdcloud.gardener.camellia.uac.account.schema.trait;

import com.jdcloud.gardener.fragrans.data.trait.contact.ContactTraits;

/**
 * @author zhanghan30
 * @date 2022/8/13 8:40 下午
 */
public interface Contact extends
        ContactTraits.MobilePhoneNumber,
        ContactTraits.OfficeTelephoneNumber,
        ContactTraits.Fax,
        ContactTraits.Email {
}
