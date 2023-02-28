package io.gardenerframework.camellia.authentication.server.main.management.endpoint;

import io.gardenerframework.camellia.authentication.server.common.api.group.AuthenticationServerRestController;
import io.gardenerframework.camellia.authentication.server.main.utils.UserAuthenticationServiceRegistry;
import io.gardenerframework.camellia.authentication.server.main.management.schema.response.GetAvailableAuthenticationTypesResponse;
import io.gardenerframework.fragrans.api.standard.error.exception.client.ForbiddenException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

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
                userAuthenticationServiceRegistry.getRegisteredAuthenticationTypes(
                        false,
                        false
                )
        );
    }

    /**
     * 激活认证服务
     *
     * @param type 认证类型
     */
    @Override
    @PostMapping("/{type}:enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void enableService(
            @Valid @NotBlank @PathVariable("type") String type
    ) {
        if (!userAuthenticationServiceRegistry.getRegisteredAuthenticationTypes(false, true).contains(type)) {
            throw new ForbiddenException(String.format("type %s requested not allowed to change enabled status", type));
        }
        userAuthenticationServiceRegistry.changeEnabledState(type, true);
    }

    /**
     * 禁用认证服务
     *
     * @param type 认证类型
     */
    @Override
    @PostMapping("/{type}:disable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disableService(
            @Valid @NotBlank @PathVariable("type") String type
    ) {
        if (!userAuthenticationServiceRegistry.getRegisteredAuthenticationTypes(false, true).contains(type)) {
            throw new ForbiddenException(String.format("type %s requested not allowed to change enabled status", type));
        }
        userAuthenticationServiceRegistry.changeEnabledState(type, false);
    }
}
