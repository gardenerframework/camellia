package com.jdcloud.gardener.camellia.uac.account.skeleton;

import com.jdcloud.gardener.camellia.uac.account.schema.request.*;
import com.jdcloud.gardener.camellia.uac.account.schema.response.AccountAppearanceTemplate;
import com.jdcloud.gardener.camellia.uac.account.schema.response.CreateAccountResponse;
import com.jdcloud.gardener.camellia.uac.common.schema.request.PaginationParameter;
import com.jdcloud.gardener.camellia.uac.common.schema.request.SecurityOperationParameter;
import com.jdcloud.gardener.camellia.uac.common.schema.response.SearchResponse;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * 账户的骨架定义
 * <p>
 * 接口和服务都实现这个定义
 *
 * @author zhanghan30
 * @date 2022/9/16 7:30 下午
 */
public interface AccountSkeletons {
    /**
     * 方法原型，主要是用于管理后台和开放的接口
     */
    interface MethodPrototype {
        /**
         * 读取账户骨架
         *
         * @param <A>
         */
        @FunctionalInterface
        interface ReadAccount<A extends AccountAppearanceTemplate> {
            /**
             * 读取指定账户信息
             *
             * @param accountId 账户id
             * @return 读取结果
             */
            A readAccount(
                    @Valid @NotBlank String accountId
            );
        }

        /**
         * 创建账户骨架
         *
         * @param <C> 创建参数类型
         */
        @FunctionalInterface
        interface CreateAccount<C extends CreateAccountParameterTemplate> {
            /**
             * 创建账户
             *
             * @param createAccountParameter 参数
             * @return 创建结果
             */
            CreateAccountResponse createAccount(
                    @Valid C createAccountParameter
            );
        }

        /**
         * 修改密码骨架
         */
        @FunctionalInterface
        interface ChangePassword {
            /**
             * 修改密码
             *
             * @param accountId               账户id
             * @param changePasswordParameter 参数
             */
            void changePassword(
                    @Valid @NotBlank String accountId,
                    @Valid ChangePasswordParameter changePasswordParameter
            );
        }

        /**
         * 激活账户
         */
        @FunctionalInterface
        interface EnableAccount {
            /**
             * 激活账户
             *
             * @param accountId                  账户id
             * @param securityOperationParameter 安全操作参数
             */
            void enableAccount(
                    @Valid @NotBlank String accountId,
                    @Valid SecurityOperationParameter securityOperationParameter
            );
        }

        /**
         * 停用账户
         */
        @FunctionalInterface
        interface DisableAccount {
            /**
             * 禁用账户
             *
             * @param accountId                  账户id
             * @param securityOperationParameter 安全操作参数
             */
            void disableAccount(
                    @Valid @NotBlank String accountId,
                    @Valid SecurityOperationParameter securityOperationParameter
            );
        }

        /**
         * 锁定账户
         */
        @FunctionalInterface
        interface LockAccount {
            /**
             * 锁定账户
             *
             * @param accountId                  账户id
             * @param securityOperationParameter 安全操作参数
             */
            void lockAccount(
                    @Valid @NotBlank String accountId,
                    @Valid SecurityOperationParameter securityOperationParameter
            );
        }

        /**
         * 解锁账户
         */
        @FunctionalInterface
        interface UnlockAccount {
            /**
             * 锁定账户
             *
             * @param accountId                  账户id
             * @param securityOperationParameter 安全操作参数
             */
            void unlockAccount(
                    @Valid @NotBlank String accountId,
                    @Valid SecurityOperationParameter securityOperationParameter
            );
        }

        /**
         * 更换手机号
         */
        @FunctionalInterface
        interface ChangeMobilePhoneNumber {
            /**
             * 更改自己的手机
             *
             * @param accountId                        账户id
             * @param changeMobilePhoneNumberParameter 参数
             */
            void changeMobilePhoneNumber(
                    @Valid @NotBlank String accountId,
                    @Valid ChangeMobilePhoneNumberParameter changeMobilePhoneNumberParameter
            );
        }

        /**
         * 变更邮箱
         */
        @FunctionalInterface
        interface ChangeEmail {
            /**
             * 更改自己的邮箱
             *
             * @param accountId            账户id
             * @param changeEmailParameter 邮箱
             */
            void changeEmail(
                    @Valid @NotBlank String accountId,
                    @Valid ChangeEmailParameter changeEmailParameter
            );
        }

        /**
         * 变更账户过期时间
         */
        @FunctionalInterface
        interface ChangeAccountExpiryDate {
            /**
             * 修改账户过期时间
             *
             * @param accountId                        账户id
             * @param changeAccountExpiryDateParameter 过期时间
             */
            void changeAccountExpiryDate(
                    @Valid @NotBlank String accountId,
                    @Valid ChangeAccountExpiryDateParameter changeAccountExpiryDateParameter
            );
        }

        /**
         * 更新账户
         *
         * @param <U> 更新类型
         */
        @FunctionalInterface
        interface UpdateAccount<U extends UpdateAccountParameterTemplate> {
            /**
             * 更新账户
             *
             * @param accountId              账户id
             * @param updateAccountParameter 参数
             */
            void updateAccount(
                    @Valid @NotBlank String accountId,
                    @Valid U updateAccountParameter
            );
        }

        /**
         * 搜索账户
         *
         * @param <S> 搜索参数类型
         * @param <A> 账户返回类型
         */
        @FunctionalInterface
        interface SearchAccount<S extends SearchAccountCriteriaParameterTemplate, A extends AccountAppearanceTemplate> {
            /**
             * 搜索账户
             *
             * @param searchAccountCriteriaParameter 搜索账户参数
             * @param paginationParameter            分页参数
             * @return 搜索结果
             */
            SearchResponse<A> searchAccount(
                    @Valid S searchAccountCriteriaParameter,
                    @Valid PaginationParameter paginationParameter
            );
        }

        /**
         * 认证账户
         *
         * @param <P> 认证参数类型
         * @param <A> 账户返回类型
         */
        @FunctionalInterface
        interface Authenticate<P extends AuthenticateAccountParameterTemplate,
                A extends AccountAppearanceTemplate> {
            /**
             * 执行认证
             *
             * @param parameter 参数
             * @return 认证成功的账户
             */
            A authenticate(@Valid P parameter);
        }
    }

    /**
     * 管理骨架
     */
    interface ManagementSkeleton<
            C extends CreateAccountParameterTemplate,
            S extends SearchAccountCriteriaParameterTemplate,
            U extends UpdateAccountParameterTemplate,
            A extends AccountAppearanceTemplate
            > extends MethodPrototype.ReadAccount<A>,
            MethodPrototype.CreateAccount<C>,
            MethodPrototype.UpdateAccount<U>,
            MethodPrototype.ChangeAccountExpiryDate,
            MethodPrototype.ChangeEmail,
            MethodPrototype.ChangeMobilePhoneNumber,
            MethodPrototype.ChangePassword,
            MethodPrototype.DisableAccount,
            MethodPrototype.EnableAccount,
            MethodPrototype.LockAccount,
            MethodPrototype.UnlockAccount,
            MethodPrototype.SearchAccount<S, A> {
    }

    /**
     * open api 的骨架定义
     */
    interface OpenApiSkeleton<
            C extends CreateAccountParameterTemplate,
            P extends AuthenticateAccountParameterTemplate,
            A extends AccountAppearanceTemplate
            > extends MethodPrototype.ReadAccount<A>,
            MethodPrototype.CreateAccount<C>,
            MethodPrototype.Authenticate<P, A>,
            MethodPrototype.ChangePassword {
    }

    /**
     * 用户自服务骨架
     */
    interface SelfServiceSkeleton<
            U extends UpdateAccountParameterTemplate,
            A extends AccountAppearanceTemplate
            > {
        /**
         * 查询个人账户
         *
         * @return 账户信息
         */

        A readAccount();

        /**
         * 变更自己的密码
         *
         * @param changePasswordParameter 密码变更请求
         */
        void changePassword(
                @Valid ChangePasswordParameter changePasswordParameter
        );

        /**
         * 更新账户信息
         *
         * @param updateAccountParameter 更新参数
         */
        void updateAccount(
                @Valid U updateAccountParameter
        );

        /**
         * 更改自己的手机
         *
         * @param changeMobilePhoneNumberParameter 参数
         */
        void changeMobilePhoneNumber(
                @Valid ChangeMobilePhoneNumberParameter changeMobilePhoneNumberParameter
        );

        /**
         * 更改自己的邮箱
         *
         * @param changeEmailParameter 邮箱
         */
        void changeEmail(
                @Valid ChangeEmailParameter changeEmailParameter
        );
    }
}
