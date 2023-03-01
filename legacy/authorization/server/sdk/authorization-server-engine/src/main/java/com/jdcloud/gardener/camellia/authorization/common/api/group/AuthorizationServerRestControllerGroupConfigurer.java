package com.jdcloud.gardener.camellia.authorization.common.api.group;

import com.jdcloud.gardener.camellia.authorization.common.configuration.AuthorizationServerPathOption;
import com.jdcloud.gardener.fragrans.api.group.ApiGroupProvider;
import com.jdcloud.gardener.fragrans.api.group.policy.ApiGroupContextPathPolicy;
import com.jdcloud.gardener.fragrans.api.group.policy.ApiGroupPolicyProvider;
import com.jdcloud.gardener.fragrans.api.options.endpoint.ApiOptionsEndpoint;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

/**
 * @author zhanghan30
 * @date 2022/5/13 12:32 上午
 */
@Component
@RequiredArgsConstructor
public class AuthorizationServerRestControllerGroupConfigurer implements ApiGroupProvider, ApiGroupPolicyProvider<ApiGroupContextPathPolicy> {
    private final AuthorizationServerPathOption authorizationServerPathOption;


    @Override
    public Class<? extends Annotation> getAnnotation() {
        return AuthorizationServerRestController.class;
    }

    @Override
    public ApiGroupContextPathPolicy getPolicy() {
        return new ApiGroupContextPathPolicy(authorizationServerPathOption.getRestApiContextPath());
    }

    @Nullable
    @Override
    public Collection<Class<?>> getAdditionalMembers() {
        return Collections.singletonList(ApiOptionsEndpoint.class);
    }
}
