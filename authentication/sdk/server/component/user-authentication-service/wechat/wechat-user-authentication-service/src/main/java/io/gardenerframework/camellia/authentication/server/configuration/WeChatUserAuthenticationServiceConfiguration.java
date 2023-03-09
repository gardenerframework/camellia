package io.gardenerframework.camellia.authentication.server.configuration;

import io.gardenerframework.camellia.authentication.server.WeChatUserAuthenticationServicePackage;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * @author zhanghan30
 * @date 2023/3/9 07:51
 */
@Configuration
@ComponentScan(basePackageClasses = WeChatUserAuthenticationServicePackage.class,
        includeFilters = {@ComponentScan.Filter(
                type = FilterType.ANNOTATION,
                value = WeChatUserAuthenticationServiceComponent.class)
        })
public class WeChatUserAuthenticationServiceConfiguration {
}
