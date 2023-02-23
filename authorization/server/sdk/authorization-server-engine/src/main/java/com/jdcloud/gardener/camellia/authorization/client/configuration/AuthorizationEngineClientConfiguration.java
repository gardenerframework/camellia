package com.jdcloud.gardener.camellia.authorization.client.configuration;

import com.jdcloud.gardener.camellia.authorization.client.AuthorizationServerEngineClientPackage;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author ZhangHan
 * @date 2022/5/11 20:38
 */
@Configuration
@ComponentScan(basePackageClasses = AuthorizationServerEngineClientPackage.class)
public class AuthorizationEngineClientConfiguration {
}
