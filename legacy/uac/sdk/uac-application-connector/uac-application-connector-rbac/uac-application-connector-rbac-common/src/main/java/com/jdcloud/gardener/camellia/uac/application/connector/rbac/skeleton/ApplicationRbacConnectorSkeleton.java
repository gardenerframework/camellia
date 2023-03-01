package com.jdcloud.gardener.camellia.uac.application.connector.rbac.skeleton;

import com.jdcloud.gardener.camellia.uac.application.connector.rbac.schema.request.BindAccountsToApplicationRolesParameter;
import com.jdcloud.gardener.camellia.uac.application.connector.rbac.schema.request.RemoveAccountsParameter;
import com.jdcloud.gardener.camellia.uac.application.connector.rbac.schema.request.SearchApplicationRoleParameter;
import com.jdcloud.gardener.camellia.uac.application.connector.rbac.schema.request.SearchBindRolesParameter;
import com.jdcloud.gardener.camellia.uac.application.connector.rbac.schema.response.SearchApplicationRoleResponse;
import com.jdcloud.gardener.camellia.uac.common.schema.request.PaginationParameter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * @author zhanghan30
 * @date 2022/11/17 19:38
 */
public interface ApplicationRbacConnectorSkeleton {

    /**
     * 对应用执行角色搜索
     *
     * @param applicationId       应用id
     * @param searchParameter     搜索参数
     * @param paginationParameter 分页参数
     * @return 搜索结果
     */
    SearchApplicationRoleResponse searchRoles(
            @Valid @NotBlank String applicationId,
            @Valid SearchApplicationRoleParameter searchParameter,
            @Valid PaginationParameter paginationParameter
    );

    /**
     * 按照用户读取绑定的角色
     *
     * @param applicationId 应用id
     * @param parameter     参数参数
     * @return 搜索结果
     */
    SearchApplicationRoleResponse searchBindRoles(
            @Valid @NotBlank String applicationId,
            @Valid SearchBindRolesParameter parameter
    );

    /**
     * 执行账户角色重新绑定
     * <p>
     * 当账户在应用中不存在时，应当执行先添加用户到应用，再执行绑定的逻辑
     *
     * @param applicationId 应用id
     * @param parameter     绑定参数
     */
    void bindRoles(
            @Valid @NotBlank String applicationId,
            @Valid BindAccountsToApplicationRolesParameter parameter
    );

    /**
     * 从应用中删除账户
     * <p>
     * 即从应用中移除授权用户
     *
     * @param applicationId 应用id
     * @param parameter     要删除的人员清单
     */
    void removeAccounts(
            @Valid @NotBlank String applicationId,
            @Valid RemoveAccountsParameter parameter
    );
}
