package com.jdcloud.gardener.camellia.uac.application.connector.rbac.configuration;

import com.jdcloud.gardener.camellia.uac.application.connector.rbac.service.ApplicationRbacConnectorServiceRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author zhanghan30
 * @date 2022/11/18 13:18
 */
@Configuration
@Import(ApplicationRbacConnectorServiceRegistry.class)
public class ApplicationRbacConnectorServiceConfiguration {
}
