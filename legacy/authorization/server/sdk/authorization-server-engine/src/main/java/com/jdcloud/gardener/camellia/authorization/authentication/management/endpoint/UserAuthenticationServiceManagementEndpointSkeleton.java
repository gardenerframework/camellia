package com.jdcloud.gardener.camellia.authorization.authentication.management.endpoint;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * @author ZhangHan
 * @date 2022/5/11 11:45
 */
public interface UserAuthenticationServiceManagementEndpointSkeleton {
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
