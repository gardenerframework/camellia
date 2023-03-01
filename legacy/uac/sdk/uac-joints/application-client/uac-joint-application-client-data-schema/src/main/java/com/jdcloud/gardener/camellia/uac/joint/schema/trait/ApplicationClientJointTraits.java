package com.jdcloud.gardener.camellia.uac.joint.schema.trait;

import com.jdcloud.gardener.fragrans.sugar.trait.annotation.Trait;

/**
 * @author ZhangHan
 * @date 2022/11/24 19:13
 */
public interface ApplicationClientJointTraits {
    @Trait
    class Application<A> {
        private A application;
    }
    @Trait
    class Client<C> {
        private C client;
    }
}
