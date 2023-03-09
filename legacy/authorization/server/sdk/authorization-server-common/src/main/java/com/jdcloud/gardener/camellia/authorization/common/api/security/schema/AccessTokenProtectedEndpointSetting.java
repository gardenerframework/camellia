package com.jdcloud.gardener.camellia.authorization.common.api.security.schema;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author ZhangHan
 * @date 2022/5/14 13:25
 */
@AllArgsConstructor
@Data
public class AccessTokenProtectedEndpointSetting {
    private final Class<?> endpoint;
    private final boolean optional;
}
