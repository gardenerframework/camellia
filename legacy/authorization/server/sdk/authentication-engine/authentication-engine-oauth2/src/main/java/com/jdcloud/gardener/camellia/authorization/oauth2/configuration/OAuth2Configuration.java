package com.jdcloud.gardener.camellia.authorization.oauth2.configuration;

import com.jdcloud.gardener.camellia.authorization.authentication.main.DefaultOAuth2StateService;
import com.jdcloud.gardener.camellia.authorization.authentication.main.OAuth2StateService;
import com.jdcloud.gardener.camellia.authorization.authentication.main.endpoint.OAuth2StateEndpoint;
import com.jdcloud.gardener.camellia.authorization.authentication.main.exception.OAuth2AuthenticationEngineExceptions;
import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.OAuth2AuthenticationServiceRegistry;
import com.jdcloud.gardener.fragrans.api.standard.error.configuration.RevealError;
import com.jdcloud.gardener.fragrans.log.GenericBasicLogger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author zhanghan30
 * @date 2022/11/11 14:53
 */
@Configuration
@RevealError(superClasses = {
        OAuth2AuthenticationEngineExceptions.ClientSideException.class,
        OAuth2AuthenticationEngineExceptions.ServerSideException.class
})
@Import({
        OAuth2StateEndpoint.class,
        OAuth2AuthenticationServiceRegistry.class
})
public class OAuth2Configuration {
    @Bean
    @ConditionalOnMissingBean(OAuth2StateService.class)
    public DefaultOAuth2StateService defaultOAuth2StateService(GenericBasicLogger basicLogger) {
        return new DefaultOAuth2StateService(basicLogger);
    }
}
