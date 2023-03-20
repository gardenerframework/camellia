package io.gardenerframework.camellia.authentication.server.configuration;

import io.gardenerframework.camellia.authentication.server.WeChatMiniProgramQrCodeAuthenticationServicePackage;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * @author zhanghan30
 * @date 2023/3/16 18:59
 */
@Configuration
@ComponentScan(
        basePackageClasses = WeChatMiniProgramQrCodeAuthenticationServicePackage.class,
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, value = WeChatMiniProgramQrCodeServiceComponent.class)
        }
)
public class WeChatMiniProgramQrCodeAuthenticationServiceConfiguration {
}
