package com.jdcloud.gardener.camellia.uac.account.endpoint;

import com.jdcloud.gardener.camellia.uac.account.schema.request.*;
import com.jdcloud.gardener.camellia.uac.account.schema.response.AccountAppearanceTemplate;
import com.jdcloud.gardener.camellia.uac.account.schema.response.CreateAccountResponse;
import com.jdcloud.gardener.camellia.uac.account.service.AccountServiceTemplate;
import com.jdcloud.gardener.camellia.uac.account.skeleton.AccountSkeletons;
import com.jdcloud.gardener.camellia.uac.common.endpoint.grouping.annotation.ManagementApi;
import com.jdcloud.gardener.camellia.uac.common.endpoint.grouping.annotation.OpenApi;
import com.jdcloud.gardener.camellia.uac.common.endpoint.grouping.annotation.SelfServiceApi;
import com.jdcloud.gardener.camellia.uac.common.schema.request.PaginationParameter;
import com.jdcloud.gardener.camellia.uac.common.schema.request.SecurityOperationParameter;
import com.jdcloud.gardener.camellia.uac.common.schema.response.SearchResponse;
import com.jdcloud.gardener.fragrans.api.advice.engine.EndpointHandlerMethodBeforeAdviceAdapter;
import com.jdcloud.gardener.fragrans.api.security.schema.Operator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * 完成接口定义工作
 *
 * @author zhanghan30
 * @date 2022/9/5 7:26 下午
 */
public interface AccountEndpointTemplates {
    /**
     * 完成用户管理动作
     * <p>
     * 用户管理动作的意思是管理员的后台操作
     *
     * @author zhanghan30
     * @date 2022/8/11 11:56 上午
     */
    @RequestMapping("/account")
    @ManagementApi
    abstract class ManagementApiEndpointTemplate<
            C extends CreateAccountParameterTemplate,
            S extends SearchAccountCriteriaParameterTemplate,
            U extends UpdateAccountParameterTemplate,
            A extends AccountAppearanceTemplate> implements
            AccountSkeletons.ManagementSkeleton<C, S, U, A> {
        @Getter(AccessLevel.PROTECTED)
        @Setter(value = AccessLevel.PRIVATE, onMethod = @__({@Autowired}))
        private AccountServiceTemplate<C, S, U, ?, A, ?, ?> accountService;

        /**
         * 从账户id读取用户信息
         *
         * @param accountId 账户id
         * @return 用户信息
         */
        @GetMapping("/{accountId}")
        @Override
        public A readAccount(
                @Valid @NotBlank @PathVariable("accountId") String accountId
        ) {
            return accountService.readAccount(accountId);
        }

        /**
         * 创建指定用户
         *
         * @param createAccountParameter 创建参数
         * @return 创建出来的用户id
         */
        @PostMapping
        @Override
        public CreateAccountResponse createAccount(
                @Valid @RequestBody C createAccountParameter
        ) {
            return accountService.createAccount(createAccountParameter);
        }

        /**
         * 搜索账户
         *
         * @param searchAccountCriteriaParameter 搜索参数
         * @param paginationParameter            分页参数
         * @return 搜索结果
         */
        @GetMapping
        @Override
        public SearchResponse<A> searchAccount(
                @Valid S searchAccountCriteriaParameter,
                @Valid PaginationParameter paginationParameter
        ) {
            return accountService.searchAccount(
                    searchAccountCriteriaParameter,
                    paginationParameter
            );
        }

        /**
         * 锁定用户
         *
         * @param accountId                  账户id
         * @param securityOperationParameter 安全操作参数
         */
        @PostMapping("/{accountId}:lock")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        @Override
        public void lockAccount(
                @Valid @NotBlank @PathVariable("accountId") String accountId,
                @Valid @RequestBody SecurityOperationParameter securityOperationParameter
        ) {
            accountService.lockAccount(accountId, securityOperationParameter);
        }

        /**
         * 解锁用户
         *
         * @param accountId                  账户id
         * @param securityOperationParameter 安全操作参数
         */
        @PostMapping("/{accountId}:unlock")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        @Override
        public void unlockAccount(
                @Valid @NotBlank @PathVariable("accountId") String accountId,
                @Valid @RequestBody SecurityOperationParameter securityOperationParameter
        ) {
            accountService.unlockAccount(accountId, securityOperationParameter);
        }

        /**
         * 启用账户
         *
         * @param accountId                  账户id
         * @param securityOperationParameter 安全操作参数
         */
        @PostMapping("/{accountId}:enable")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        @Override
        public void enableAccount(
                @Valid @NotBlank @PathVariable("accountId") String accountId,
                @Valid @RequestBody SecurityOperationParameter securityOperationParameter
        ) {
            accountService.enableAccount(accountId, securityOperationParameter);
        }

        /**
         * 停用账户
         *
         * @param accountId                  账户id
         * @param securityOperationParameter 安全操作参数
         */
        @PostMapping("/{accountId}:disable")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        @Override
        public void disableAccount(
                @Valid @NotBlank @PathVariable("accountId") String accountId,
                @Valid @RequestBody SecurityOperationParameter securityOperationParameter
        ) {
            accountService.disableAccount(accountId, securityOperationParameter);
        }

        /**
         * 由管理员修改密码
         *
         * @param accountId               账户id
         * @param changePasswordParameter 修改密码
         */
        @PutMapping("/{accountId}/password")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        @Override
        public void changePassword(
                @Valid @NotBlank @PathVariable("accountId") String accountId,
                @Valid @RequestBody ChangePasswordParameter changePasswordParameter
        ) {
            accountService.changePassword(accountId, changePasswordParameter);
        }

        /**
         * 修改手机号
         * <p>
         * 使用{@link EndpointHandlerMethodBeforeAdviceAdapter}配合实现{@link AccountSkeletons.SelfServiceSkeleton}完成对挑眼的校验
         *
         * @param accountId                        账户id
         * @param changeMobilePhoneNumberParameter 参数
         */
        @PutMapping("/{accountId}/mobilePhoneNumber")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        @Override
        public void changeMobilePhoneNumber(
                @Valid @NotBlank @PathVariable("accountId") String accountId,
                @Valid @RequestBody ChangeMobilePhoneNumberParameter changeMobilePhoneNumberParameter
        ) {
            accountService.changeMobilePhoneNumber(accountId, changeMobilePhoneNumberParameter);
        }

        /**
         * 修改邮箱
         * <p>
         * 使用{@link EndpointHandlerMethodBeforeAdviceAdapter}配合实现{@link AccountSkeletons.SelfServiceSkeleton}完成对挑眼的校验
         *
         * @param accountId            账户id
         * @param changeEmailParameter 参数
         */
        @PutMapping("/{accountId}/email")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        @Override
        public void changeEmail(
                @Valid @NotBlank @PathVariable("accountId") String accountId,
                @Valid @RequestBody ChangeEmailParameter changeEmailParameter) {
            accountService.changeEmail(accountId, changeEmailParameter);
        }

        /**
         * 由管理员来修改账户过期时间
         *
         * @param accountId                        账户id
         * @param changeAccountExpiryDateParameter 账户过期时间
         */
        @PutMapping("/{accountId}/accountExpiryDate")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        @Override
        public void changeAccountExpiryDate(
                @Valid @NotBlank @PathVariable("accountId") String accountId,
                @Valid @RequestBody ChangeAccountExpiryDateParameter changeAccountExpiryDateParameter
        ) {
            accountService.changeAccountExpiryDate(accountId, changeAccountExpiryDateParameter);
        }

        /**
         * 由管理员修改用户信息
         *
         * @param accountId              账户id
         * @param updateAccountParameter 更新用户信息参数
         */

        @PutMapping("/{accountId}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        @Override
        public void updateAccount(
                @Valid @NotBlank @PathVariable("accountId") String accountId,
                @Valid @RequestBody U updateAccountParameter
        ) {
            accountService.updateAccount(accountId, updateAccountParameter);
        }

        /**
         * 删除指定用户
         *
         * @param id 账户id
         */

        @DeleteMapping("/{id}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void deleteAccount(
                @Valid @NotBlank @PathVariable("id") String id
        ) {

        }
    }

    /**
     * @author zhanghan30
     * @date 2022/8/13 10:56 上午
     */

    @OpenApi
    abstract class OpenApiEndpointTemplate<
            C extends CreateAccountParameterTemplate,
            P extends AuthenticateAccountParameterTemplate,
            A extends AccountAppearanceTemplate
            > implements
            AccountSkeletons.OpenApiSkeleton<C, P, A> {
        @Getter(AccessLevel.PROTECTED)
        @Setter(value = AccessLevel.PRIVATE, onMethod = @__({@Autowired}))
        private AccountServiceTemplate<C, ?, ?, P, A, ?, ?> accountService;

        @Override
        @GetMapping("/account/{accountId}")
        public A readAccount(@Valid @NotBlank @PathVariable("accountId") String accountId) {
            return accountService.readAccount(accountId);
        }

        @Override
        @PostMapping("/account")
        public CreateAccountResponse createAccount(@Valid @RequestBody C parameter) {
            return accountService.createAccount(parameter);
        }

        @PostMapping("/account:authenticate")
        @Override
        public A authenticate(@Valid @RequestBody P parameter) {
            return accountService.authenticate(parameter);
        }

        @PutMapping("/account/{accountId}/password")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        @Override
        public void changePassword(
                @Valid @NotBlank @PathVariable("accountId") String accountId,
                @Valid @RequestBody ChangePasswordParameter changePasswordParameter
        ) {
            accountService.changePassword(accountId, changePasswordParameter);
        }
    }

    /**
     * 用户自服务的接口，主要是查询自己的信息，修改自己的信息，重置密码等
     *
     * @author zhanghan30
     * @date 2022/8/12 9:09 下午
     */
    @RequestMapping("/account")
    @SelfServiceApi
    abstract class SelfServiceEndpointTemplate<
            U extends UpdateAccountParameterTemplate,
            A extends AccountAppearanceTemplate
            > implements AccountSkeletons.SelfServiceSkeleton<U, A> {
        /**
         * 操作人
         * <p>
         * 也就是用户自己
         */
        @Getter(AccessLevel.PROTECTED)
        @Setter(value = AccessLevel.PRIVATE, onMethod = @__({@Autowired}))
        private Operator operator;
        /**
         * 这里只关心账户的vo和更新参数
         */
        @Getter(AccessLevel.PROTECTED)
        @Setter(value = AccessLevel.PRIVATE, onMethod = @__({@Autowired}))
        private AccountServiceTemplate<?, ?, U, ?, A, ?, ?> accountService;

        /**
         * 查询个人账户
         *
         * @return 账户信息
         */
        @GetMapping
        @Override
        public A readAccount() {
            return accountService.readAccount(operator.getUserId());
        }

        /**
         * 变更自己的密码
         *
         * @param changePasswordParameter 密码变更请求
         */
        @PutMapping("/password")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        @Override
        public void changePassword(
                @Valid @RequestBody ChangePasswordParameter changePasswordParameter
        ) {
            accountService.changePassword(operator.getUserId(), changePasswordParameter);
        }

        /**
         * 更新账户信息
         *
         * @param updateAccountParameter 更新参数
         */
        @PutMapping
        @ResponseStatus(HttpStatus.NO_CONTENT)
        @Override
        public void updateAccount(
                @Valid @RequestBody U updateAccountParameter
        ) {
            accountService.updateAccount(operator.getUserId(), updateAccountParameter);
        }

        /**
         * 修改手机号
         * <p>
         * 使用{@link EndpointHandlerMethodBeforeAdviceAdapter}配合实现{@link AccountSkeletons.SelfServiceSkeleton}完成对挑眼的校验
         *
         * @param changeMobilePhoneNumberParameter 参数
         */
        @PutMapping("/mobilePhoneNumber")
        @Override
        public void changeMobilePhoneNumber(@Valid @RequestBody ChangeMobilePhoneNumberParameter changeMobilePhoneNumberParameter) {
            accountService.changeMobilePhoneNumber(operator.getUserId(), changeMobilePhoneNumberParameter);
        }

        /**
         * 修改邮箱
         * <p>
         * 使用{@link EndpointHandlerMethodBeforeAdviceAdapter}配合实现{@link AccountSkeletons.SelfServiceSkeleton}完成对挑眼的校验
         *
         * @param changeEmailParameter 参数
         */
        @PutMapping("/email")
        @Override
        public void changeEmail(@Valid @RequestBody ChangeEmailParameter changeEmailParameter) {
            accountService.changeEmail(operator.getUserId(), changeEmailParameter);
        }
    }
}
