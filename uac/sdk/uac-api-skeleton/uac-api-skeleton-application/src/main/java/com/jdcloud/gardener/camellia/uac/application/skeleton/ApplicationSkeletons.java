package com.jdcloud.gardener.camellia.uac.application.skeleton;

import com.jdcloud.gardener.camellia.uac.application.schema.request.CreateApplicationParameterTemplate;
import com.jdcloud.gardener.camellia.uac.application.schema.request.SearchApplicationCriteriaParameterTemplate;
import com.jdcloud.gardener.camellia.uac.application.schema.request.UpdateApplicationParameterTemplate;
import com.jdcloud.gardener.camellia.uac.application.schema.response.ApplicationAppearanceTemplate;
import com.jdcloud.gardener.camellia.uac.common.schema.request.PaginationParameter;
import com.jdcloud.gardener.camellia.uac.common.schema.request.SecurityOperationParameter;
import com.jdcloud.gardener.camellia.uac.common.schema.response.SearchResponse;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * @author ZhangHan
 * @date 2022/11/8 17:10
 */
public interface ApplicationSkeletons {
    interface MethodPrototype {
        /**
         * 读取应用信息
         *
         * @param <A> 应用VO
         */
        interface ReadApplication<A extends ApplicationAppearanceTemplate> {
            /**
             * 读取指定账户信息
             *
             * @param applicationId 应用id
             * @return 读取结果
             */
            A readApplication(
                    @Valid @NotBlank String applicationId
            );
        }

        /**
         * 创建应用
         *
         * @param <C> 应用dto
         */
        interface CreateApplication<C extends CreateApplicationParameterTemplate> {
            /**
             * 创建应用
             *
             * @param createApplicationParameter 创建参数
             */
            void createApplication(
                    @Valid C createApplicationParameter
            );
        }

        /**
         * 更新应用
         *
         * @param <U> 更新参数类型
         */
        interface UpdateApplication<U extends UpdateApplicationParameterTemplate> {
            /**
             * 更新应用
             *
             * @param applicationId              应用id
             * @param updateApplicationParameter 更新应用参数
             */
            void updateApplication(
                    @Valid @NotBlank String applicationId,
                    @Valid U updateApplicationParameter
            );
        }

        /**
         * 激活应用
         */
        interface EnableApplication {
            /**
             * 启用应用
             *
             * @param applicationId              应用id
             * @param securityOperationParameter 安全操作参数
             */
            void enableApplication(
                    @Valid @NotBlank String applicationId,
                    @Valid SecurityOperationParameter securityOperationParameter
            );
        }

        /**
         * 禁用应用
         */
        interface DisableApplication {
            /**
             * 禁用应用
             *
             * @param applicationId              应用id
             * @param securityOperationParameter 安全操作参数
             */
            void disableApplication(
                    @Valid @NotBlank String applicationId,
                    @Valid SecurityOperationParameter securityOperationParameter
            );
        }

        /**
         * 搜索应用
         *
         * @param <S> 搜索参数
         * @param <A> 应用VO
         */
        interface SearchApplication<S extends SearchApplicationCriteriaParameterTemplate, A extends ApplicationAppearanceTemplate> {

            /**
             * 搜索应用
             *
             * @param searchApplicationCriteriaParameter 搜索应用参数
             * @param paginationParameter                分页参数
             * @return 搜索结果
             */
            SearchResponse<A> searchApplication(
                    @Valid S searchApplicationCriteriaParameter,
                    @Valid PaginationParameter paginationParameter
            );
        }
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
            C extends CreateApplicationParameterTemplate,
            S extends SearchApplicationCriteriaParameterTemplate,
            U extends UpdateApplicationParameterTemplate,
            A extends ApplicationAppearanceTemplate
            > extends MethodPrototype.ReadApplication<A>,
            MethodPrototype.CreateApplication<C>,
            MethodPrototype.UpdateApplication<U>,
            MethodPrototype.EnableApplication,
            MethodPrototype.DisableApplication,
            MethodPrototype.SearchApplication<S, A> {


    }

    /**
     * 开放接口
     *
     * @param <A>
     */
    interface OpenApiSkeleton<A extends ApplicationAppearanceTemplate> extends MethodPrototype.ReadApplication<A> {

    }
}
