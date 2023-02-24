package io.gardenerframework.camellia.authentication.server.common.api.group;

import io.gardenerframework.camellia.authentication.server.common.configuration.AuthenticationServerPathOption;
import io.gardenerframework.fragrans.api.group.ApiGroupProvider;
import io.gardenerframework.fragrans.api.group.policy.ApiGroupContextPathPolicy;
import io.gardenerframework.fragrans.api.group.policy.ApiGroupPolicyProvider;
import io.gardenerframework.fragrans.api.options.endpoint.ApiOptionsEndpoint;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

/**
 * @author zhanghan30
 * @date 2022/5/13 12:32 上午
 */
@RequiredArgsConstructor
public class AuthorizationServerRestControllerGroupConfigurer implements ApiGroupProvider, ApiGroupPolicyProvider<ApiGroupContextPathPolicy> {
    private final AuthenticationServerPathOption authenticationServerPathOption;


    @Override
    public Class<? extends Annotation> getAnnotation() {
        return AuthenticationServerRestController.class;
    }

    @Override
    public ApiGroupContextPathPolicy getPolicy() {
        return new ApiGroupContextPathPolicy(authenticationServerPathOption.getRestApiContextPath());
    }

    @Nullable
    @Override
    public Collection<Class<?>> getAdditionalMembers() {
        return Collections.singletonList(ApiOptionsEndpoint.class);
    }
}
