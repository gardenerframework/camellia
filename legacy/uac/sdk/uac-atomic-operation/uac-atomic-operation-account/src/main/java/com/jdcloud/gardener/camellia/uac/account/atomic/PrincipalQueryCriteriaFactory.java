package com.jdcloud.gardener.camellia.uac.account.atomic;

import com.jdcloud.gardener.camellia.uac.account.schema.criteria.AccountCriteriaTemplate;
import com.jdcloud.gardener.camellia.uac.account.schema.entity.AccountEntityTemplate;

import java.util.Collection;

/**
 * @author zhanghan30
 * @date 2022/11/4 15:17
 */
public interface PrincipalQueryCriteriaFactory<A extends AccountEntityTemplate, C extends AccountCriteriaTemplate> {
    /**
     * 返回一个按手机号查询账号的条件
     *
     * @param mobilePhoneNumber 手机号
     * @return 查询条件
     */
    C createQueryCriteriaByAccountByMobilePhoneNumber(
            String mobilePhoneNumber
    );

    /**
     * 返回一个按邮箱查询账号的条件
     *
     * @param email 手机号
     * @return 查询条件
     */
    C createQueryCriteriaByAccountByEmail(
            String email
    );

    /**
     * 基于账户创建查询条件
     *
     * @param account 账户信息
     * @return 查询条件
     */
    C createQueryCriteriaByAccount(A account);

    /**
     * 获取账号的trait清单，用于查询的should逻辑
     *
     * @return trait清单
     */
    Collection<Class<?>> getPrincipalFieldTraits();
}
