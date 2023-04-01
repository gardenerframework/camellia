package io.gardenerframework.camellia.authentication.server.common.api.group;

import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEngineComponent;
import io.gardenerframework.camellia.authentication.server.common.configuration.AuthenticationServerPathOption;
import io.gardenerframework.fragrans.api.group.ApiGroupProvider;
import io.gardenerframework.fragrans.api.group.policy.ApiGroupContextPathPolicy;
import io.gardenerframework.fragrans.api.group.policy.ApiGroupPolicyProvider;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;

/**
 * @author zhanghan30
 * @date 2023/3/21 19:44
 */
@RequiredArgsConstructor
@AuthenticationServerEngineComponent
public class AuthenticationServerAdministrationRestControllerGroupConfigurer
        implements ApiGroupProvider, ApiGroupPolicyProvider<ApiGroupContextPathPolicy> {
    private final AuthenticationServerPathOption authenticationServerPathOption;

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return AuthenticationServerAdministrationRestController.class;
    }

    @Override
    public ApiGroupContextPathPolicy getPolicy() {
        return new ApiGroupContextPathPolicy(authenticationServerPathOption.getAdministrationRestApiContextPath());
    }
}
