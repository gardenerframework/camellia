package com.jdcloud.gardener.camellia.uac.application.connector.rbac.endpoint.validation;

import com.jdcloud.gardener.camellia.uac.application.connector.rbac.endpoint.ApplicationRbacConnectorEndpointBase;
import com.jdcloud.gardener.camellia.uac.application.connector.rbac.schema.request.BindAccountsToApplicationRolesParameter;
import com.jdcloud.gardener.camellia.uac.application.connector.rbac.schema.request.RemoveAccountsParameter;
import com.jdcloud.gardener.camellia.uac.application.connector.rbac.schema.request.SearchApplicationRoleParameter;
import com.jdcloud.gardener.camellia.uac.application.connector.rbac.schema.request.SearchBindRolesParameter;
import com.jdcloud.gardener.camellia.uac.application.connector.rbac.schema.response.SearchApplicationRoleResponse;
import com.jdcloud.gardener.camellia.uac.application.connector.rbac.skeleton.ApplicationRbacConnectorSkeleton;
import com.jdcloud.gardener.camellia.uac.common.schema.request.PaginationParameter;
import com.jdcloud.gardener.fragrans.api.advice.engine.EndpointHandlerMethodBeforeAdviceAdapter;
import com.jdcloud.gardener.fragrans.api.standard.error.exception.client.BadRequestArgumentException;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * @author zhanghan30
 * @date 2022/11/18 13:42
 */
public class ApplicationIdValidationAdvice extends
        EndpointHandlerMethodBeforeAdviceAdapter implements ApplicationRbacConnectorSkeleton {
    private final ApplicationIdChecker applicationIdChecker;

    public ApplicationIdValidationAdvice(ApplicationIdChecker applicationIdChecker) {
        super(ApplicationRbacConnectorEndpointBase.class);
        this.applicationIdChecker = applicationIdChecker;
    }

    @Override
    public SearchApplicationRoleResponse searchRoles(@Valid @NotBlank String applicationId, @Valid SearchApplicationRoleParameter searchParameter, @Valid PaginationParameter paginationParameter) {
        if (!applicationIdChecker.check(applicationId)) {
            throw new BadRequestArgumentException(applicationId);
        }
        return null;
    }

    @Override
    public SearchApplicationRoleResponse searchBindRoles(@Valid @NotBlank String applicationId, @Valid SearchBindRolesParameter searchBindRolesParameter) {
        if (!applicationIdChecker.check(applicationId)) {
            throw new BadRequestArgumentException(applicationId);
        }
        return null;
    }

    @Override
    public void bindRoles(@Valid @NotBlank String applicationId, @Valid BindAccountsToApplicationRolesParameter bindParameter) {
        if (!applicationIdChecker.check(applicationId)) {
            throw new BadRequestArgumentException(applicationId);
        }
    }

    @Override
    public void removeAccounts(@Valid @NotBlank String applicationId, @Valid RemoveAccountsParameter parameter) {
        if (!applicationIdChecker.check(applicationId)) {
            throw new BadRequestArgumentException(applicationId);
        }
    }

}
