package io.gardenerframework.camellia.authentication.server.main.management.endpoint;

import io.gardenerframework.camellia.authentication.server.main.management.schema.response.GetAvailableAuthenticationTypesResponse;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * @author ZhangHan
 * @date 2022/5/11 11:45
 */
public interface UserAuthenticationServiceManagementEndpointSkeleton {
    /**
     * 获取所有认证服务类型
     *
     * @return 当前激活的服务类型
     */
    GetAvailableAuthenticationTypesResponse getAvailableTypes();

    /**
     * 激活转换器
     *
     * @param type 认证类型
     */
    void enableService(
            @Valid @NotBlank String type
    );

    /**
     * 禁用转换器
     *
     * @param type 认证类型
     */

    void disableService(
            @Valid @NotBlank String type
    );
}
