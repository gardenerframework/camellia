package com.jdcloud.gardener.camellia.uac.client.configuration;

import com.jdcloud.gardener.camellia.uac.client.defaults.atomic.DefaultClientAtomicOperation;
import com.jdcloud.gardener.camellia.uac.client.defaults.dao.mapper.DefaultClientMapperPackage;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author zhanghan30
 * @date 2022/11/7 11:57
 */
@Configuration
@Import({
        ClientAtomicOperationConfiguration.ApplicationAtomicOperationDefaultsConfiguration.class
})
public class ClientAtomicOperationConfiguration {
    @Configuration
    @MapperScan(basePackageClasses = DefaultClientMapperPackage.class)
    @Import({
            DefaultClientAtomicOperation.class
    })
    public static class ApplicationAtomicOperationDefaultsConfiguration {

    }
}
