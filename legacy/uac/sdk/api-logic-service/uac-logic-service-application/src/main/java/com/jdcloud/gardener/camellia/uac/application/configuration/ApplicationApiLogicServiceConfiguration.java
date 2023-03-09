package com.jdcloud.gardener.camellia.uac.application.configuration;

import com.jdcloud.gardener.camellia.uac.application.defaults.service.DefaultApplicationService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author ZhangHan
 * @date 2022/11/8 11:16
 */
@Configuration
@Import(ApplicationApiLogicServiceConfiguration.ApplicationApiLogicServiceDefaultConfiguration.class)
public class ApplicationApiLogicServiceConfiguration {

    @Configuration
    @Import(DefaultApplicationService.class)
    public static class ApplicationApiLogicServiceDefaultConfiguration {

    }
}
