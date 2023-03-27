package io.gardenerframework.camellia.authentication.server.configuration;

import io.gardenerframework.camellia.authentication.server.AdministrationServerEnginePackage;
import io.gardenerframework.camellia.authentication.server.common.annotation.AdministrationServerComponent;
import io.gardenerframework.camellia.authentication.server.common.configuration.AdministrationServerPathOption;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

/**
 * @author zhanghan30
 * @date 2023/3/22 17:01
 */
@Configuration
@Import({
        AdministrationServerPathOption.class
})
@ComponentScan(basePackageClasses = AdministrationServerEnginePackage.class, includeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = AdministrationServerComponent.class)
})
public class AdministrationServerEngineConfiguration {
}
