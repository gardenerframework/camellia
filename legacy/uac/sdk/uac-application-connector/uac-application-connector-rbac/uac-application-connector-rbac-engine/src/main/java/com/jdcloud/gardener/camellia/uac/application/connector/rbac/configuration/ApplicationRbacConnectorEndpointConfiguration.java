package com.jdcloud.gardener.camellia.uac.application.connector.rbac.configuration;

import com.jdcloud.gardener.camellia.uac.application.connector.rbac.endpoint.validation.ApplicationIdChecker;
import com.jdcloud.gardener.camellia.uac.application.connector.rbac.endpoint.validation.ApplicationIdValidationAdvice;
import com.jdcloud.gardener.camellia.uac.application.connector.rbac.exception.ApplicationRbacConnectorExceptions;
import com.jdcloud.gardener.camellia.uac.application.service.ApplicationServiceTemplate;
import com.jdcloud.gardener.fragrans.api.standard.error.configuration.RevealError;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author zhanghan30
 * @date 2022/11/18 13:46
 */
@Configuration
@Import(ApplicationIdValidationAdvice.class)
@RevealError(superClasses = {
        ApplicationRbacConnectorExceptions.ClientSideException.class,
        ApplicationRbacConnectorExceptions.ServerSideException.class,
        ApplicationRbacConnectorEndpointConfiguration.ApplicationServiceAwareApplicationIdCheckerFactory.class
})
public class ApplicationRbacConnectorEndpointConfiguration {
    //这个注解要写在类上才不报错，不知道为什么
    @ConditionalOnClass(ApplicationServiceTemplate.class)
    @SuppressWarnings("rawtypes")
    public static class ApplicationServiceAwareApplicationIdCheckerFactory {
        @Bean
        public ApplicationIdChecker applicationServiceAwareApplicationIdChecker(
                ApplicationServiceTemplate template
        ) {
            //通过服务检查应用id是否存在
            return applicationId -> template.readApplication(applicationId) != null;
        }
    }


    @Bean
    @ConditionalOnMissingBean(ApplicationIdChecker.class)
    public ApplicationIdChecker defaultNoopChecker() {
        return applicationId -> true;
    }
}
