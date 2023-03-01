package com.jdcloud.gardener.camellia.authorization.authentication.configuration;

import com.jdcloud.gardener.camellia.authorization.authentication.main.UsernamePasswordAuthenticationParameterPostProcessor;
import com.jdcloud.gardener.camellia.authorization.authentication.main.UsernamePasswordAuthenticationServiceBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhanghan30
 * @date 2021/12/31 7:33 下午
 */
@Configuration
@Slf4j
public class UsernamePasswordAuthenticationEngineConfiguration implements ApplicationContextAware, InitializingBean {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String[] beans = applicationContext.getBeanNamesForType(UsernamePasswordAuthenticationServiceBase.class);
        if (beans.length == 0) {
            throw new IllegalStateException("no subclass bean of UsernamePasswordAuthenticationServiceBase found");
        }
    }

    @Bean
    @ConditionalOnMissingBean(UsernamePasswordAuthenticationParameterPostProcessor.class)
    public UsernamePasswordAuthenticationParameterPostProcessor noopUsernamePasswordAuthenticationParameterPostProcessor() {
        return parameter -> {
        };
    }

}
