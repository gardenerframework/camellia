package com.jdcloud.gardener.camellia.uac.account.schema.trait;

import com.jdcloud.gardener.fragrans.data.trait.personal.PersonalTraits;

/**
 * 基本个人信息
 *
 * @author zhanghan30
 * @date 2022/8/13 8:30 下午
 */
public interface BasicPersonalInformation extends
        PersonalTraits.Surname,
        PersonalTraits.GivenName,
        PersonalTraits.Gender,
        PersonalTraits.DateOfBirth,
        PersonalTraits.EthnicGroup {
}
