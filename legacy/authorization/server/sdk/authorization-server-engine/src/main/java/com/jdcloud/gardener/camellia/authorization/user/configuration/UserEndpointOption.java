package com.jdcloud.gardener.camellia.authorization.user.configuration;

import com.jdcloud.gardener.fragrans.api.options.schema.ApiOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author ZhangHan
 * @date 2022/5/14 0:47
 */
@ApiOption(readonly = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class UserEndpointOption {
    /**
     * 当遇到带有令牌访问的请求时重定向oauth2的userinfo接口
     */
    private boolean redirectToOidcUserInfoEndpoint = true;
}
