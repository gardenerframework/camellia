package com.jdcloud.gardener.camellia.authorization.authentication.management.endpoint;

import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.UserAuthenticationServiceRegistry;
import com.jdcloud.gardener.camellia.authorization.common.api.group.AuthorizationServerRestController;
import com.jdcloud.gardener.camellia.authorization.common.api.security.AccessTokenProtectedEndpoint;
import com.jdcloud.gardener.fragrans.api.standard.error.exception.client.ForbiddenException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * 用于启用和停用转换器
 *
 * @author zhanghan30
 * @date 2022/4/25 1:17 下午
 */
@Component
@AuthorizationServerRestController
@AllArgsConstructor
@AccessTokenProtectedEndpoint
@RequestMapping("/authentication/service")
public class UserAuthenticationServiceManagementEndpoint implements UserAuthenticationServiceManagementEndpointSkeleton {
    private final UserAuthenticationServiceRegistry userAuthenticationServiceRegistry;

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
