package com.jdcloud.gardener.camellia.authorization.authentication.main.schema;

import com.jdcloud.gardener.camellia.authorization.authentication.main.UserAuthenticationService;
import com.jdcloud.gardener.camellia.authorization.authentication.main.annotation.AuthenticationEndpoint;
import com.jdcloud.gardener.camellia.authorization.authentication.main.annotation.AuthenticationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.Nullable;

/**
 * @author ZhangHan
 * @date 2022/5/12 0:41
 */
@Data
@AllArgsConstructor
public class UserAuthenticationServiceRegistryItem {
    /**
     * 转换器
     */
    private final UserAuthenticationService service;
    /**
     * 注解
     */
    private final AuthenticationType authenticationType;
    @Nullable
    private final AuthenticationEndpoint authenticationEndpoint;
    /**
     * 是否是工程自保留的
     */
    private final boolean preserved;
    /**
     * 是否激活的标记
     */
    private boolean enabled;
}
