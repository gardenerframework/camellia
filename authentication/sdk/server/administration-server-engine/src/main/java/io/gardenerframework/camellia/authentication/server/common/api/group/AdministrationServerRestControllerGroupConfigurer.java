package io.gardenerframework.camellia.authentication.server.common.api.group;

import io.gardenerframework.camellia.authentication.server.common.annotation.AdministrationServerComponent;
import io.gardenerframework.camellia.authentication.server.common.configuration.AdministrationServerPathOption;
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
@AdministrationServerComponent
public class AdministrationServerRestControllerGroupConfigurer
        implements ApiGroupProvider, ApiGroupPolicyProvider<ApiGroupContextPathPolicy> {
    private final AdministrationServerPathOption administrationServerPathOption;

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return AdministrationServerRestController.class;
    }

    @Override
    public ApiGroupContextPathPolicy getPolicy() {
        return new ApiGroupContextPathPolicy(administrationServerPathOption.getRestApiContextPath());
    }
}
