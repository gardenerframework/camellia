package com.jdcloud.gardener.camellia.uac.application.connector.rbac.exception.client;

import com.jdcloud.gardener.camellia.uac.application.connector.rbac.exception.ApplicationRbacConnectorExceptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author zhanghan30
 * @date 2022/11/17 20:55
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ApplicationRbacConnectorServiceNotFoundException extends ApplicationRbacConnectorExceptions.ClientSideException {
    private final String applicationId;

    public ApplicationRbacConnectorServiceNotFoundException(String applicationId) {
        super(applicationId);
        this.applicationId = applicationId;
    }
}
