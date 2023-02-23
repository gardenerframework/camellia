package com.jdcloud.gardener.camellia.uac.joint.schema.trait;

import com.jdcloud.gardener.fragrans.sugar.trait.annotation.Trait;

import java.util.Date;

/**
 * @author zhanghan30
 * @date 2022/9/9 5:57 下午
 */
public interface JointTraits {
    interface AggregationTraits {
        @Trait
        class Account<A> {
            private A account;
        }

        @Trait
        class Application<A> {
            private A application;
        }
    }

    @Trait
    class AccountOrganizationRelationExpiryDate {
        /**
         * 账户与组织的关系过期时间
         */
        private Date accountOrganizationRelationExpiryDate;
    }

    @Trait
    class AccountRoleRelationExpiryDate {
        /**
         * 账户与角色的关系过期时间
         */
        private Date accountRoleRelationExpiryDate;
    }

    @Trait
    class AccountOrganizationRoleRelationExpiryDate {
        /**
         * 账户在组织内的角色的关系过期时间
         */
        private Date accountOrganizationRoleRelationExpiryDate;
    }

    @Trait
    class AccountApplicationRelationExpiryDate {
        /**
         * 账户在应用的关系过期时间
         */
        private Date accountApplicationRelationExpiryDate;
    }
}
