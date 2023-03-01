package com.jdcloud.gardener.camellia.authorization.common.configuration;

import com.jdcloud.gardener.camellia.authorization.common.AuthorizationServerEngineCommonPackage;
import com.jdcloud.gardener.camellia.authorization.common.api.security.AccessTokenProtectedEndpointSupplier;
import com.jdcloud.gardener.camellia.authorization.common.api.security.schema.AccessTokenProtectedEndpointSetting;
import com.jdcloud.gardener.camellia.authorization.common.event.AuthorizationServerEventListenerFactory;
import com.jdcloud.gardener.fragrans.api.options.endpoint.ApiOptionsEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhanghan30
 * @date 2022/4/20 12:47 下午
 */
@Configuration
@ComponentScan(basePackageClasses = AuthorizationServerEngineCommonPackage.class)
public class AuthorizationServerEngineCommonConfiguration implements AccessTokenProtectedEndpointSupplier {
    /**
     * 生成默认配置
     *
     * @return 默认配置
     */
    @Bean
    @ConditionalOnMissingBean(AuthorizationServerPathOption.class)
    public AuthorizationServerPathOption authorizationServerPathOption() {
        return new AuthorizationServerPathOption();
    }

    /**
     * 声明自己的事件工厂
     * <p>
     * 内部是的非bean可能会用到日志，因此声明了对日志的依赖
     *
     * @return 工厂
     */
    @Bean
    public AuthorizationServerEventListenerFactory authorizationServerEventListenerFactory() {
        return new AuthorizationServerEventListenerFactory();
    }

    @Override
    public AccessTokenProtectedEndpointSetting getAccessTokenProtectedEndpoint() {
        return new AccessTokenProtectedEndpointSetting(
                ApiOptionsEndpoint.class,
                //网页端可以不需要令牌
                true
        );
    }
}
