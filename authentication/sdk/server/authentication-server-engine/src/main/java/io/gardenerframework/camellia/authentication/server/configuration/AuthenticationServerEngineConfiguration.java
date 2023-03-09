package io.gardenerframework.camellia.authentication.server.configuration;

import io.gardenerframework.camellia.authentication.server.AuthenticationServerEnginePackage;
import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEngineComponent;
import io.gardenerframework.camellia.authentication.server.common.configuration.AuthenticationServerPathOption;
import io.gardenerframework.camellia.authentication.server.main.utils.RequestingClientHolder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

/**
 * 主配置类
 *
 * @author zhanghan30
 * @date 2023/2/23 14:50
 */
@Configuration
@Import({
        //配置累
        AuthenticationServerEngineSecurityConfiguration.class,
        AuthenticationServerEngineOAuth2ComponentConfiguration.class,
        //配置类
        AuthenticationServerPathOption.class,
        //工具类
        RequestingClientHolder.class
})
@ComponentScan(basePackageClasses = AuthenticationServerEnginePackage.class, includeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = AuthenticationServerEngineComponent.class)
})
public class AuthenticationServerEngineConfiguration {
}
