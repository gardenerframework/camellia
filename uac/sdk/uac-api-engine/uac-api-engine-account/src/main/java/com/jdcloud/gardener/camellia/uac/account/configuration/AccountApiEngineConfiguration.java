package com.jdcloud.gardener.camellia.uac.account.configuration;

import com.jdcloud.gardener.camellia.uac.account.defualts.endpoints.AccountDefaultEndpoints;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author ZhangHan
 * @date 2022/11/8 11:16
 */
@Configuration
@Import({
        AccountApiEngineConfiguration.AccountApiEngineDefaultConfiguration.class
})
@AutoConfigureAfter(AccountApiLogicServiceConfiguration.class)
public class AccountApiEngineConfiguration {
    @Configuration
    @Import({
            AccountDefaultEndpoints.DefaultManagementApiEndpoint.class,
            AccountDefaultEndpoints.DefaultOpenApiEndpoint.class,
            AccountDefaultEndpoints.DefaultSelfServiceEndpoint.class
    })
    public static class AccountApiEngineDefaultConfiguration {

    }
}
