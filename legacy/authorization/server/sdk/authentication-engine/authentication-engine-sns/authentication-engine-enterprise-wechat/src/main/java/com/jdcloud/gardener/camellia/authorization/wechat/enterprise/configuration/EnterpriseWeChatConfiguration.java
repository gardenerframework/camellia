package com.jdcloud.gardener.camellia.authorization.wechat.enterprise.configuration;

import com.jdcloud.gardener.camellia.authorization.authentication.main.EnterpriseWeChatAuthenticationService;
import com.jdcloud.gardener.camellia.authorization.wechat.enterprise.EnterpriseWeChatPackage;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author ZhangHan
 * @date 2022/11/8 21:29
 */
@Configuration
@ComponentScan(basePackageClasses = EnterpriseWeChatPackage.class)
@Import(EnterpriseWeChatAuthenticationService.class)
public class EnterpriseWeChatConfiguration {
}
