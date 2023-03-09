package com.jdcloud.gardener.camellia.uac.application.configuration;

import com.jdcloud.gardener.camellia.uac.application.defaults.endpoint.ApplicationDefaultEndpoints;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author ZhangHan
 * @date 2022/11/8 11:16
 */
@Configuration
@Import(ApplicationApiEngineConfiguration.ApplicationApiEngineDefaultConfiguration.class)
public class ApplicationApiEngineConfiguration {
    @Configuration
    @Import({
            ApplicationDefaultEndpoints.DefaultManagementApiEndpoint.class,
            ApplicationDefaultEndpoints.DefaultOpenApiEndpoint.class
    })
    public static class ApplicationApiEngineDefaultConfiguration {

    }
}
