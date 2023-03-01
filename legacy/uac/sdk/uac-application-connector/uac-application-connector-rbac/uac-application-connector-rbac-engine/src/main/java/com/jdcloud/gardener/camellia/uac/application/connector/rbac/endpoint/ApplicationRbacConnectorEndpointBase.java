package com.jdcloud.gardener.camellia.uac.application.connector.rbac.endpoint;

import com.jdcloud.gardener.camellia.uac.application.connector.rbac.exception.client.ApplicationRbacConnectorServiceNotFoundException;
import com.jdcloud.gardener.camellia.uac.application.connector.rbac.schema.request.BindAccountsToApplicationRolesParameter;
import com.jdcloud.gardener.camellia.uac.application.connector.rbac.schema.request.RemoveAccountsParameter;
import com.jdcloud.gardener.camellia.uac.application.connector.rbac.schema.request.SearchApplicationRoleParameter;
import com.jdcloud.gardener.camellia.uac.application.connector.rbac.schema.request.SearchBindRolesParameter;
import com.jdcloud.gardener.camellia.uac.application.connector.rbac.schema.response.SearchApplicationRoleResponse;
import com.jdcloud.gardener.camellia.uac.application.connector.rbac.service.ApplicationRbacConnectorService;
import com.jdcloud.gardener.camellia.uac.application.connector.rbac.service.ApplicationRbacConnectorServiceRegistry;
import com.jdcloud.gardener.camellia.uac.application.connector.rbac.skeleton.ApplicationRbacConnectorSkeleton;
import com.jdcloud.gardener.camellia.uac.common.schema.request.PaginationParameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

/**
 * @author zhanghan30
 * @date 2022/11/17 20:08
 */
@RequiredArgsConstructor
public abstract class ApplicationRbacConnectorEndpointBase implements ApplicationRbacConnectorSkeleton {
    private final ApplicationRbacConnectorServiceRegistry serviceRegistry;

    /**
     * 查询角色
     *
     * @param applicationId       应用id
     * @param searchParameter     查询参数
     * @param paginationParameter 分页参数
     * @return 查询结果
     */
    @Override
    @GetMapping("/{applicationId}/role")
    public SearchApplicationRoleResponse searchRoles(
            @Valid @NotBlank @PathVariable("applicationId") String applicationId,
            @Valid SearchApplicationRoleParameter searchParameter,
            @Valid PaginationParameter paginationParameter) {
        return Objects.requireNonNull(lookupService(applicationId).searchRoles(applicationId, searchParameter, paginationParameter));
    }

    /**
     * 按照账户id查询绑定的角色
     *
     * @param applicationId            应用id
     * @param searchBindRolesParameter 账户参数
     * @return 结果
     */
    @Override
    @GetMapping("/{applicationId}/joint:account+role")
    public SearchApplicationRoleResponse searchBindRoles(
            @Valid @NotBlank @PathVariable("applicationId") String applicationId,
            @Valid SearchBindRolesParameter searchBindRolesParameter) {
        return Objects.requireNonNull(lookupService(applicationId).searchBindRoles(applicationId, searchBindRolesParameter));
    }

    /**
     * 绑定角色
     *
     * @param applicationId 应用id
     * @param bindParameter 绑定参数
     */
    @Override
    @PostMapping("/{applicationId}/joint:account+role")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void bindRoles(
            @Valid @NotBlank @PathVariable("applicationId") String applicationId,
            @Valid @RequestBody BindAccountsToApplicationRolesParameter bindParameter
    ) {
        lookupService(applicationId).bindRoles(applicationId, bindParameter);
    }

    /**
     * 删除给定应用下的账号
     * <p>
     * 部分应用会服从权限系统的应用账户管理，不需要从连接器进行删除同步
     * <p>
     * 除非本身权限系统没有应用以及人员和应用关系的管理能力
     *
     * @param applicationId 应用id
     * @param parameter     参数
     */
    @Override
    @DeleteMapping("/{applicationId}/account")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAccounts(
            @Valid @NotBlank @PathVariable("applicationId") String applicationId,
            @Valid @RequestBody RemoveAccountsParameter parameter
    ) {
        lookupService(applicationId).removeAccounts(applicationId, parameter);
    }

    /**
     * 查询服务
     *
     * @param applicationId 应用id
     * @return 服务
     */
    private ApplicationRbacConnectorService lookupService(String applicationId) {
        ApplicationRbacConnectorService service = serviceRegistry.get(applicationId);
        if (service == null) {
            //应用不存在
            throw new ApplicationRbacConnectorServiceNotFoundException(applicationId);
        }
        return service;
    }
}
