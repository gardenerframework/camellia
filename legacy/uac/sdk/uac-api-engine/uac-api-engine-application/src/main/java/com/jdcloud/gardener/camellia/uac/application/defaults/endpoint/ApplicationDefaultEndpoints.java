package com.jdcloud.gardener.camellia.uac.application.defaults.endpoint;

import com.jdcloud.gardener.camellia.uac.application.defautls.schema.request.DefaultCreateApplicationParameter;
import com.jdcloud.gardener.camellia.uac.application.defautls.schema.request.DefaultSearchApplicationCriteriaParameter;
import com.jdcloud.gardener.camellia.uac.application.defautls.schema.request.DefaultUpdateApplicationParameter;
import com.jdcloud.gardener.camellia.uac.application.defautls.schema.response.DefaultApplicationAppearance;
import com.jdcloud.gardener.camellia.uac.application.endpoint.ApplicationEndpointTemplates;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhanghan30
 * @date 2022/11/9 14:49
 */
public interface ApplicationDefaultEndpoints {
    @RestController
    @ConditionalOnMissingBean(value = ApplicationEndpointTemplates.ManagementApiEndpointTemplate.class, ignored = DefaultManagementApiEndpoint.class)
    class DefaultManagementApiEndpoint extends ApplicationEndpointTemplates.ManagementApiEndpointTemplate<
            DefaultCreateApplicationParameter,
            DefaultSearchApplicationCriteriaParameter,
            DefaultUpdateApplicationParameter,
            DefaultApplicationAppearance
            > {
    }

    @RestController
    @ConditionalOnMissingBean(value = ApplicationEndpointTemplates.OpenApiTemplate.class, ignored = DefaultOpenApiEndpoint.class)
    class DefaultOpenApiEndpoint extends ApplicationEndpointTemplates.OpenApiTemplate<DefaultApplicationAppearance> {
    }
}
