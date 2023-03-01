package com.jdcloud.gardener.camellia.uac.account.defualts.endpoints;

import com.jdcloud.gardener.camellia.uac.account.defaults.schema.request.DefaultAuthenticateAccountParameter;
import com.jdcloud.gardener.camellia.uac.account.defaults.schema.request.DefaultCreateAccountParameter;
import com.jdcloud.gardener.camellia.uac.account.defaults.schema.request.DefaultSearchAccountCriteriaParameter;
import com.jdcloud.gardener.camellia.uac.account.defaults.schema.request.DefaultUpdateAccountParameter;
import com.jdcloud.gardener.camellia.uac.account.defaults.schema.response.DefaultAccountAppearance;
import com.jdcloud.gardener.camellia.uac.account.defualts.service.DefaultAccountService;
import com.jdcloud.gardener.camellia.uac.account.endpoint.AccountEndpointTemplates;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.RestController;

/**
 * 默认接口组
 *
 * @author ZhangHan
 * @date 2022/11/8 11:22
 */
public interface AccountDefaultEndpoints {
    @ConditionalOnMissingBean(value = AccountEndpointTemplates.ManagementApiEndpointTemplate.class, ignored = DefaultManagementApiEndpoint.class)
    @ConditionalOnBean(value = DefaultAccountService.class)
    @RestController
    class DefaultManagementApiEndpoint extends AccountEndpointTemplates.ManagementApiEndpointTemplate<
            DefaultCreateAccountParameter,
            DefaultSearchAccountCriteriaParameter,
            DefaultUpdateAccountParameter,
            DefaultAccountAppearance> {
    }

    @ConditionalOnMissingBean(value = AccountEndpointTemplates.OpenApiEndpointTemplate.class, ignored = DefaultOpenApiEndpoint.class)
    @ConditionalOnBean(value = DefaultAccountService.class)
    @RestController
    class DefaultOpenApiEndpoint extends AccountEndpointTemplates.OpenApiEndpointTemplate<
            DefaultCreateAccountParameter,
            DefaultAuthenticateAccountParameter,
            DefaultAccountAppearance
            > {
    }

    @ConditionalOnMissingBean(value = AccountEndpointTemplates.SelfServiceEndpointTemplate.class, ignored = DefaultSelfServiceEndpoint.class)
    @ConditionalOnBean(value = DefaultAccountService.class)
    @RestController
    class DefaultSelfServiceEndpoint extends AccountEndpointTemplates.SelfServiceEndpointTemplate<
            DefaultUpdateAccountParameter,
            DefaultAccountAppearance> {
    }
}
