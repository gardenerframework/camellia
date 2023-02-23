package com.jdcloud.gardener.camellia.uac.client.skeleton;

import com.jdcloud.gardener.camellia.uac.client.schema.request.*;
import com.jdcloud.gardener.camellia.uac.client.schema.response.ClientAppearanceTemplate;
import com.jdcloud.gardener.camellia.uac.client.schema.response.CreateClientResponse;
import com.jdcloud.gardener.camellia.uac.common.schema.request.PaginationParameter;
import com.jdcloud.gardener.camellia.uac.common.schema.request.SecurityOperationParameter;
import com.jdcloud.gardener.camellia.uac.common.schema.response.SearchResponse;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * @author ZhangHan
 * @date 2022/11/8 17:10
 */
public interface ClientSkeletons {
    /**
     * 通用骨架
     */
    interface CommonSkeleton<A extends ClientAppearanceTemplate> {
        /**
         * 读取指定客户端信息
         *
         * @param clientId 客户端id
         * @return 读取结果
         */
        A readClient(
                @Valid @NotBlank String clientId
        );
    }

    /**
     * 管理接口
     *
     * @param <C>
     * @param <S>
     * @param <U>
     * @param <A>
     */
    interface ManagementSkeleton<
            C extends CreateClientParameterTemplate,
            S extends SearchClientCriteriaParameterTemplate,
            U extends UpdateClientParameterTemplate,
            A extends ClientAppearanceTemplate
            > extends CommonSkeleton<A> {
        /**
         * 创建客户端
         * <p>
         * 单独创建不怎么常见，一般都是指定应用后创建
         * <p>
         * 可以在安全访问层抛出403来阻断访问
         *
         * @param createClientParameter 创建参数
         */
        CreateClientResponse createClient(
                @Valid C createClientParameter
        );

        /**
         * 更新客户端
         * <p>
         * 超级管理员从体验上自然可以不管什么应用的id，直接给一个客户端id就能操作
         *
         * @param clientId              客户端id
         * @param updateClientParameter 更新参数
         */
        void updateClient(
                @Valid @NotBlank String clientId,
                @Valid U updateClientParameter
        );

        /**
         * 启用客户端
         *
         * @param clientId                   客户端id
         * @param securityOperationParameter 安全操作参数
         */
        void enableClient(
                @Valid @NotBlank String clientId,
                @Valid SecurityOperationParameter securityOperationParameter
        );

        /**
         * 禁用客户端
         *
         * @param clientId                   客户端id
         * @param securityOperationParameter 安全操作参数
         */
        void disableClient(
                @Valid @NotBlank String clientId,
                @Valid SecurityOperationParameter securityOperationParameter
        );

        /**
         * 变更客户端自动获取客户批准的
         *
         * @param clientId                   客户端id
         * @param securityOperationParameter 安全操作参数
         */
        void disableClientAuthorizationAutoConsent(
                @Valid @NotBlank String clientId,
                @Valid SecurityOperationParameter securityOperationParameter
        );

        /**
         * 变更客户端自动获取客户批准的
         *
         * @param clientId                   客户端id
         * @param securityOperationParameter 安全操作参数
         */
        void enableClientAuthorizationAutoConsent(
                @Valid @NotBlank String clientId,
                @Valid SecurityOperationParameter securityOperationParameter
        );

        /**
         * 变更客户端授权范围
         *
         * @param clientId             客户端id
         * @param changeScopeParameter 参数
         */
        void changeClientScope(
                @Valid @NotBlank String clientId,
                @Valid ChangeScopeParameter changeScopeParameter
        );

        /**
         * 变更客户端授权类型
         *
         * @param clientId                 客户端id
         * @param changeGrantTypeParameter 参数
         */
        void changeClientGrantType(
                @Valid @NotBlank String clientId,
                @Valid ChangeGrantTypeParameter changeGrantTypeParameter
        );

        /**
         * 更改客户端的重定向地址
         *
         * @param clientId                   客户端id
         * @param changeRedirectUriParameter 参数
         */
        void changeClientRedirectUri(
                @Valid @NotBlank String clientId,
                @Valid ChangeRedirectUriParameter changeRedirectUriParameter
        );

        /**
         * 搜索客户端
         *
         * @param searchClientCriteriaParameter 搜索参数
         * @param paginationParameter           分页参数
         * @return 搜索结果
         */
        SearchResponse<A> searchClient(
                @Valid S searchClientCriteriaParameter,
                @Valid PaginationParameter paginationParameter
        );
    }

    /**
     * 开放接口
     *
     * @param <A>
     */
    interface OpenApiSkeleton<A extends ClientAppearanceTemplate> extends CommonSkeleton<A> {
        /**
         * 执行认证
         *
         * @param parameter 参数
         * @return 认证成功的客户端
         */
        A authenticate(@Valid AuthenticateClientParameter parameter);
    }
}
