package com.jdcloud.gardener.camellia.authorization.authentication.configuration;

import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.UserAuthenticationServiceRegistry;
import com.jdcloud.gardener.fragrans.api.options.schema.ApiOption;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * 认证类型注册表
 *
 * @author zhanghan30
 * @date 2022/1/3 8:41 下午
 */
@ApiOption(readonly = true)
@AllArgsConstructor
@Component
public class AuthenticationTypeRegistry {
    private final UserAuthenticationServiceRegistry registry;

    /**
     * 获取认证类型清单
     *
     * @return 清单
     */
    public Collection<String> getTypes() {
        return getTypes(false, false);
    }

    /**
     * 获取认证类型清单
     *
     * @param showPreserved 是否展示保留的
     * @param showDisabled  是否展示非激活的
     * @return 清单
     */
    public Collection<String> getTypes(boolean showPreserved, boolean showDisabled) {
        return registry.getRegisteredAuthenticationTypes(showPreserved, showDisabled);
    }
}
