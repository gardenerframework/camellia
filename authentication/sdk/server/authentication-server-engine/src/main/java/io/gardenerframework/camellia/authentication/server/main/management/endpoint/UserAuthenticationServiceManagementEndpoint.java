package io.gardenerframework.camellia.authentication.server.main.management.endpoint;

import io.gardenerframework.camellia.authentication.server.common.api.group.AuthenticationServerRestController;
import io.gardenerframework.camellia.authentication.server.main.management.schema.response.GetAvailableAuthenticationTypesResponse;
import io.gardenerframework.camellia.authentication.server.main.utils.UserAuthenticationServiceRegistry;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 用于启用和停用转换器
 *
 * @author zhanghan30
 * @date 2022/4/25 1:17 下午
 */
@AuthenticationServerRestController
@AllArgsConstructor
@RequestMapping("/authentication/service")
public class UserAuthenticationServiceManagementEndpoint implements UserAuthenticationServiceManagementEndpointSkeleton {
    private final UserAuthenticationServiceRegistry userAuthenticationServiceRegistry;

    /**
     * 返回所有可用的认证服务类型
     *
     * @return 类型清单
     */
    @Override
    @GetMapping
    public GetAvailableAuthenticationTypesResponse getAvailableTypes() {
        return new GetAvailableAuthenticationTypesResponse(
                userAuthenticationServiceRegistry.getUserAuthenticationServiceTypes()
        );
    }
}
