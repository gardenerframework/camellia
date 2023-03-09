package com.jdcloud.gardener.camellia.uac.common.endpoint.grouping;

import com.jdcloud.gardener.camellia.uac.common.endpoint.grouping.annotation.SelfServiceApi;
import com.jdcloud.gardener.fragrans.api.group.ApiGroupProvider;
import com.jdcloud.gardener.fragrans.api.group.policy.ApiGroupContextPathPolicy;
import com.jdcloud.gardener.fragrans.api.group.policy.ApiGroupPolicyProvider;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

/**
 * @author zhanghan30
 * @date 2022/8/18 3:33 上午
 */
@Component
public class SelfServiceApiGroupProvider implements ApiGroupProvider, ApiGroupPolicyProvider<ApiGroupContextPathPolicy> {
    @Override
    public Class<? extends Annotation> getAnnotation() {
        return SelfServiceApi.class;
    }

    @Override
    public ApiGroupContextPathPolicy getPolicy() {
        return new ApiGroupContextPathPolicy("/me");
    }
}
