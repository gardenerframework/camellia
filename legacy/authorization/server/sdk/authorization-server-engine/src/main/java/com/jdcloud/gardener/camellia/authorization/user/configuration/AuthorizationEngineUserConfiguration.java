package com.jdcloud.gardener.camellia.authorization.user.configuration;

import com.jdcloud.gardener.camellia.authorization.user.AuthorizationServerEngineUserPackage;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author ZhangHan
 * @date 2022/5/11 20:38
 */
@Configuration
@ComponentScan(basePackageClasses = AuthorizationServerEngineUserPackage.class)
public class AuthorizationEngineUserConfiguration {
}
