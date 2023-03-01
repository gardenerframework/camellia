package com.jdcloud.gardener.camellia.uac.application.connector.rbac.test.endpoint;

import com.jdcloud.gardener.camellia.uac.application.connector.rbac.endpoint.ApplicationRbacConnectorEndpointBase;
import com.jdcloud.gardener.camellia.uac.application.connector.rbac.service.ApplicationRbacConnectorServiceRegistry;
import com.jdcloud.gardener.camellia.uac.common.endpoint.grouping.annotation.ManagementApi;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhanghan30
 * @date 2022/11/18 14:52
 */
@RestController
@Component
@RequestMapping("/application")
@ManagementApi
public class ApplicationRbacConnectorTestEndpoint extends ApplicationRbacConnectorEndpointBase {
    public ApplicationRbacConnectorTestEndpoint(ApplicationRbacConnectorServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }
}
