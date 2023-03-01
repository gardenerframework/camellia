package com.jdcloud.gardener.camellia.uac.account.configuration;

import com.jdcloud.gardener.camellia.uac.account.defualts.service.DefaultAccountService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author ZhangHan
 * @date 2022/11/8 11:16
 */
@Configuration
@Import({
        AccountApiLogicServiceConfiguration.AccountApiLogicServiceDefaultConfiguration.class,
        AccountSecurityOption.class
})
public class AccountApiLogicServiceConfiguration {
    @Configuration
    @Import(DefaultAccountService.class)
    public static class AccountApiLogicServiceDefaultConfiguration {
    }
}
