package com.jdcloud.gardener.camellia.uac.application.configuration;

import com.jdcloud.gardener.camellia.uac.application.atomic.ApplicationAtomicOperationTemplate;
import com.jdcloud.gardener.camellia.uac.application.defaults.atomic.DefaultApplicationAtomicOperation;
import com.jdcloud.gardener.camellia.uac.application.defaults.dao.mapper.DefaultApplicationMapperPackage;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author zhanghan30
 * @date 2022/11/7 11:57
 */
@Configuration
@Import({
        ApplicationAtomicOperationConfiguration.ApplicationAtomicOperationDefaultsConfiguration.class
})
public class ApplicationAtomicOperationConfiguration {
    @Configuration
    @MapperScan(basePackageClasses = DefaultApplicationMapperPackage.class)
    @Import({
            DefaultApplicationAtomicOperation.class
    })
    public static class ApplicationAtomicOperationDefaultsConfiguration {

    }
}
